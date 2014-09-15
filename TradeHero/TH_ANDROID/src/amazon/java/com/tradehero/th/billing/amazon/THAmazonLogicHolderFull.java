package com.tradehero.th.billing.amazon;

import android.content.Intent;
import android.content.res.Resources;
import com.tradehero.common.billing.amazon.AmazonPurchaseConsumer;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUList;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.common.utils.ArrayUtils;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THBaseBillingLogicHolder;
import com.tradehero.th.billing.THProductDetailDomainPredicate;
import com.tradehero.th.billing.amazon.request.THAmazonRequest;
import com.tradehero.th.billing.amazon.request.THAmazonRequestFull;
import com.tradehero.th.persistence.billing.AmazonSKUListCache;
import com.tradehero.th.persistence.billing.THAmazonProductDetailCache;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class THAmazonLogicHolderFull
    extends THBaseBillingLogicHolder<
        AmazonSKUListKey,
        AmazonSKU,
        AmazonSKUList,
        THAmazonProductDetail,
        THAmazonProductDetailTuner,
        THAmazonPurchaseOrder,
        THAmazonOrderId,
        THAmazonPurchase,
        THAmazonRequestFull,
        AmazonException>
    implements THAmazonLogicHolder
{
    @NotNull private final THAmazonPurchaseConsumerHolder thAmazonPurchaseConsumerHolder;

    //<editor-fold desc="Constructors">
    @Inject public THAmazonLogicHolderFull(
            @NotNull AmazonSKUListCache amazonSKUListCache,
            @NotNull THAmazonProductDetailCache thskuDetailCache,
            @NotNull THAmazonBillingAvailableTesterHolder thAmazonBillingAvailableTesterHolder,
            @NotNull THAmazonProductIdentifierFetcherHolder thAmazonProductIdentifierFetcherHolder,
            @NotNull THAmazonInventoryFetcherHolder thAmazonInventoryFetcherHolder,
            @NotNull THAmazonPurchaseFetcherHolder thAmazonPurchaseFetcherHolder,
            @NotNull THAmazonPurchaserHolder thAmazonPurchaserHolder,
            @NotNull THAmazonPurchaseReporterHolder thAmazonPurchaseReporterHolder,
            @NotNull THAmazonPurchaseConsumerHolder thAmazonPurchaseConsumerHolder)
    {
        super(
                amazonSKUListCache,
                thskuDetailCache,
                thAmazonBillingAvailableTesterHolder,
                thAmazonProductIdentifierFetcherHolder,
                thAmazonInventoryFetcherHolder,
                thAmazonPurchaseFetcherHolder,
                thAmazonPurchaserHolder,
                thAmazonPurchaseReporterHolder);
        this.thAmazonPurchaseConsumerHolder = thAmazonPurchaseConsumerHolder;
    }
    //</editor-fold>

    //<editor-fold desc="Life Cycle">
    @Override public void onDestroy()
    {
        thAmazonPurchaseConsumerHolder.onDestroy();
        super.onDestroy();
    }
    //</editor-fold>

    @Override public String getBillingHolderName(Resources resources)
    {
        return resources.getString(R.string.th_amazon_logic_holder_name);
    }

    //<editor-fold desc="Request Code Management">
    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return super.isUnusedRequestCode(requestCode)
                && thAmazonPurchaseConsumerHolder.isUnusedRequestCode(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        super.forgetRequestCode(requestCode);
        thAmazonPurchaseConsumerHolder.forgetRequestCode(requestCode);
    }
    //</editor-fold>

    @Override public void registerListeners(int requestCode, @NotNull THAmazonRequestFull billingRequest)
    {
        super.registerListeners(requestCode, billingRequest);
        registerConsumptionFinishedListener(requestCode, billingRequest.consumptionFinishedListener);
    }

    @Override public List<THAmazonProductDetail> getDetailsOfDomain(ProductIdentifierDomain domain)
    {
        List<THAmazonProductDetail> details = productDetailCache.get(THBaseAmazonProductIdentifierFetcher.getAllSkus());
        if (details == null)
        {
            return null;
        }
        return ArrayUtils.filter(
                details,
                new THProductDetailDomainPredicate<AmazonSKU, THAmazonProductDetail>(domain));
    }

    //<editor-fold desc="Run Logic">
    @Override protected boolean runInternal(int requestCode)
    {
        boolean launched = super.runInternal(requestCode);
        THAmazonRequestFull billingRequest = billingRequests.get(requestCode);
        if (!launched && billingRequest != null)
        {
            if (billingRequest.consumePurchase && billingRequest.purchaseToConsume != null)
            {
                launchConsumeSequence(requestCode, billingRequest.purchaseToConsume);
                launched = true;
            }
            else if (billingRequest.restorePurchase && billingRequest.fetchedPurchases != null)
            {
                boolean prepared = billingRequest.fetchedPurchases.size() > 0 && prepareToRestoreOnePurchase(requestCode, billingRequest);
                if (prepared)
                {
                    launched = runInternal(requestCode);
                }

                if (!launched)
                {
                    notifyPurchaseRestored(requestCode, billingRequest.restoredPurchases, billingRequest.restoreFailedPurchases, billingRequest.restoreFailedErrors);
                }
            }
        }
        return launched;
    }
    //</editor-fold>

    //<editor-fold desc="Sequence Logic">
    @Override protected void prepareRequestForNextRunAfterPurchaseFetchedSuccess(int requestCode, List<THAmazonPurchase> purchases)
    {
        super.prepareRequestForNextRunAfterPurchaseFetchedSuccess(requestCode, purchases);
    }

    @Override protected void prepareRequestForNextRunAfterPurchaseFinished(int requestCode, THAmazonPurchaseOrder purchaseOrder, THAmazonPurchase purchase)
    {
        super.prepareRequestForNextRunAfterPurchaseFinished(requestCode, purchaseOrder, purchase);
        THAmazonRequestFull billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.purchaseToReport = purchase;
        }
    }

    @Override protected void prepareRequestForNextRunAfterPurchaseReportedSuccess(int requestCode, THAmazonPurchase reportedPurchase, UserProfileDTO updatedUserPortfolio)
    {
        super.prepareRequestForNextRunAfterPurchaseReportedSuccess(requestCode, reportedPurchase,
                updatedUserPortfolio);
        THAmazonRequestFull billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.purchaseToConsume = reportedPurchase;
        }
    }

    @Override protected void prepareRequestForNextRunAfterPurchaseReportedFailed(int requestCode, THAmazonPurchase reportedPurchase, AmazonException error)
    {
        super.prepareRequestForNextRunAfterPurchaseReportedFailed(requestCode, reportedPurchase, error);
        THAmazonRequestFull billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            if (billingRequest.restorePurchase)
            {
                Timber.e(error, "Failed to report a purchase to be restored");
                billingRequest.restoreFailedPurchases.add(reportedPurchase);
                billingRequest.restoreFailedErrors.add(error);
                prepareToRestoreOnePurchase(requestCode, billingRequest);
            }
        }
    }

    protected void handlePurchaseConsumed(int requestCode, THAmazonPurchase purchase)
    {
        notifyPurchaseConsumed(requestCode, purchase);
        prepareRequestForNextRunAfterPurchaseConsumed(requestCode, purchase);
        runInternal(requestCode);
    }

    protected void handlePurchaseNeedNotBeConsumed(int requestCode, THAmazonPurchase purchase)
    {
        prepareRequestForNextRunAfterPurchaseConsumed(requestCode, purchase);
        runInternal(requestCode);
    }

    protected void prepareRequestForNextRunAfterPurchaseConsumed(int requestCode, THAmazonPurchase purchase)
    {
        THAmazonRequestFull billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.consumePurchase = false;
            if (billingRequest.restorePurchase)
            {
                if (purchase != null && purchase.shouldConsume())
                {
                    billingRequest.restoredPurchases.add(purchase);
                }
                prepareToRestoreOnePurchase(requestCode, billingRequest);
            }
        }
    }

    protected void handlePurchaseConsumedFailed(int requestCode, THAmazonPurchase purchase, AmazonException exception)
    {
        notifyPurchaseConsumedFailed(requestCode, purchase, exception);
        prepareRequestForNextRunAfterPurchaseConsumedFailed(requestCode, purchase, exception);
        runInternal(requestCode);
    }

    protected void prepareRequestForNextRunAfterPurchaseConsumedFailed(int requestCode, THAmazonPurchase purchase, AmazonException exception)
    {
        THAmazonRequestFull billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.consumePurchase = false;
            if (billingRequest.restorePurchase)
            {
                Timber.e(exception, "Failed to consume a purchase to be restored");
                billingRequest.restoreFailedPurchases.add(purchase);
                prepareToRestoreOnePurchase(requestCode, billingRequest);
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="Fetch Inventory">
    @Override protected void handleInventoryFetchedSuccess(int requestCode, List<AmazonSKU> productIdentifiers, Map<AmazonSKU, THAmazonProductDetail> inventory)
    {
        super.handleInventoryFetchedSuccess(requestCode, productIdentifiers, inventory);
    }
    //</editor-fold>

    //<editor-fold desc="Consume Purchase">
    @Override public AmazonPurchaseConsumer.OnAmazonConsumptionFinishedListener<AmazonSKU, THAmazonOrderId, THAmazonPurchase, AmazonException> getConsumptionFinishedListener(int requestCode)
    {
        THAmazonRequestFull billingRequest = billingRequests.get(requestCode);
        if (billingRequest == null)
        {
            return null;
        }
        return billingRequest.consumptionFinishedListener;
    }

    @Override public void registerConsumptionFinishedListener(int requestCode,
            AmazonPurchaseConsumer.OnAmazonConsumptionFinishedListener<AmazonSKU, THAmazonOrderId, THAmazonPurchase, AmazonException> consumptionFinishedListener)
    {
        THAmazonRequest billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.consumptionFinishedListener = consumptionFinishedListener;
            thAmazonPurchaseConsumerHolder.registerConsumptionFinishedListener(requestCode, createPurchaseConsumptionListener());
        }
    }

    protected AmazonPurchaseConsumer.OnAmazonConsumptionFinishedListener<AmazonSKU, THAmazonOrderId, THAmazonPurchase, AmazonException> createPurchaseConsumptionListener()
    {
        return new AmazonPurchaseConsumer.OnAmazonConsumptionFinishedListener<AmazonSKU, THAmazonOrderId, THAmazonPurchase, AmazonException>()
        {
            @Override public void onPurchaseConsumed(int requestCode, THAmazonPurchase purchase)
            {
                handlePurchaseConsumed(requestCode, purchase);
            }

            @Override public void onPurchaseConsumeFailed(int requestCode, THAmazonPurchase purchase, AmazonException exception)
            {
                handlePurchaseConsumedFailed(requestCode, purchase, exception);
            }
        };
    }

    @Override public void unregisterPurchaseConsumptionListener(int requestCode)
    {
        thAmazonPurchaseConsumerHolder.forgetRequestCode(requestCode);
        THAmazonRequestFull billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.consumptionFinishedListener = null;
        }
    }

    protected void notifyPurchaseConsumed(int requestCode, THAmazonPurchase purchase)
    {
        AmazonPurchaseConsumer.OnAmazonConsumptionFinishedListener<AmazonSKU, THAmazonOrderId, THAmazonPurchase, AmazonException> consumptionFinishedListener = getConsumptionFinishedListener(requestCode);
        if (consumptionFinishedListener != null)
        {
            consumptionFinishedListener.onPurchaseConsumed(requestCode, purchase);
        }
        unregisterPurchaseConsumptionListener(requestCode);
    }

    protected void notifyPurchaseConsumedFailed(int requestCode, THAmazonPurchase purchase, AmazonException exception)
    {
        AmazonPurchaseConsumer.OnAmazonConsumptionFinishedListener<AmazonSKU, THAmazonOrderId, THAmazonPurchase, AmazonException> consumptionFinishedListener = getConsumptionFinishedListener(requestCode);
        if (consumptionFinishedListener != null)
        {
            consumptionFinishedListener.onPurchaseConsumeFailed(requestCode, purchase, exception);
        }
        unregisterPurchaseConsumptionListener(requestCode);
    }
    //</editor-fold>

    //<editor-fold desc="Launch Sequence Methods">
    @Override public void launchConsumeSequence(int requestCode, THAmazonPurchase purchase)
    {
        if (purchase != null && purchase.shouldConsume())
        {
            thAmazonPurchaseConsumerHolder.launchConsumeSequence(requestCode, purchase);
        }
        else
        {
            handlePurchaseNeedNotBeConsumed(requestCode, purchase);
        }
    }
    //</editor-fold>

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Nothing to do
    }
}
