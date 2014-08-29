package com.tradehero.th.billing.amazon;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import com.amazon.device.iap.model.RequestId;
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
import com.tradehero.th.billing.amazon.request.THAmazonRequestFull;
import com.tradehero.th.persistence.billing.AmazonSKUListCache;
import com.tradehero.th.persistence.billing.THAmazonProductDetailCache;
import com.tradehero.th.utils.dagger.ForUIThread;
import java.util.HashMap;
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
    @NotNull protected final Handler uiHandler;

    @NotNull protected Map<RequestId, Integer> requestIdsToCode;

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
            @NotNull @ForUIThread Handler uiHandler)
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
        this.uiHandler = uiHandler;
        this.requestIdsToCode = new HashMap<>();
    }
    //</editor-fold>

    @Override public String getBillingHolderName(Resources resources)
    {
        return resources.getString(R.string.th_amazon_logic_holder_name);
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
            if (billingRequest.restorePurchase && billingRequest.fetchedPurchases != null)
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

    //<editor-fold desc="Launch Sequence Methods">
    /**
     * We need to post otherwise the iap helper may forget the listener if this method is launched right after another
     * @param requestCode
     */
    @Override public void launchBillingAvailableTestSequence(final int requestCode)
    {
        uiHandler.post(new Runnable()
        {
            public void run()
            {
                THAmazonLogicHolderFull.super.launchBillingAvailableTestSequence(requestCode);
            }
        });
    }

    /**
     * We need to post otherwise the iap helper may forget the listener if this method is launched right after another
     * @param requestCode
     */
    @Override public void launchProductIdentifierFetchSequence(final int requestCode)
    {
        uiHandler.post(new Runnable()
        {
            public void run()
            {
                THAmazonLogicHolderFull.super.launchProductIdentifierFetchSequence(requestCode);
            }
        });
    }

    /**
     * We need to post otherwise the iap helper may forget the listener if this method is launched right after another
     * @param requestCode
     * @param allIds
     */
    @Override public void launchInventoryFetchSequence(final int requestCode, final List<AmazonSKU> allIds)
    {
        uiHandler.post(new Runnable()
        {
            public void run()
            {
                THAmazonLogicHolderFull.super.launchInventoryFetchSequence(requestCode, allIds);
            }
        });
    }

    /**
     * We need to post otherwise the iap helper may forget the listener if this method is launched right after another
     * @param requestCode
     * @param purchaseOrder
     */
    @Override public void launchPurchaseSequence(final int requestCode, final THAmazonPurchaseOrder purchaseOrder)
    {
        uiHandler.post(new Runnable()
        {
            public void run()
            {
                THAmazonLogicHolderFull.super.launchPurchaseSequence(requestCode, purchaseOrder);
            }
        });
    }

    /**
     * We need to post otherwise the iap helper may forget the listener if this method is launched right after another
     * @param requestCode
     */
    @Override public void launchFetchPurchaseSequence(final int requestCode)
    {
        uiHandler.post(new Runnable()
        {
            @Override public void run()
            {
                THAmazonLogicHolderFull.super.launchFetchPurchaseSequence(requestCode);
            }
        });
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
    //</editor-fold>

    //<editor-fold desc="Fetch Inventory">
    @Override protected void handleInventoryFetchedSuccess(int requestCode, List<AmazonSKU> productIdentifiers, Map<AmazonSKU, THAmazonProductDetail> inventory)
    {
        super.handleInventoryFetchedSuccess(requestCode, productIdentifiers, inventory);
    }
    //</editor-fold>

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Nothing to do
    }
}
