package com.tradehero.th.billing.googleplay;

import android.content.Intent;
import android.content.res.Resources;
import com.tradehero.common.billing.BillingAvailableTesterHolder;
import com.tradehero.common.billing.ProductDetailCache;
import com.tradehero.common.billing.ProductIdentifierListCache;
import com.tradehero.common.billing.googleplay.BaseIABBillingAvailableTesterHolder;
import com.tradehero.common.billing.googleplay.BaseIABSKUList;
import com.tradehero.common.billing.googleplay.IABConstants;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumerHolder;
import com.tradehero.common.billing.googleplay.IABPurchaserHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.utils.ArrayUtils;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THBaseBillingLogicHolder;
import com.tradehero.th.billing.googleplay.request.THIABBillingRequestFull;
import com.tradehero.th.billing.samsung.THProductDetailDomainPredicate;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.billing.googleplay.IABSKUListCache;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCache;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class THIABLogicHolderFull
    extends THBaseBillingLogicHolder<
                IABSKUListKey,
                IABSKU,
                IABSKUList,
                THIABProductDetail,
                THIABProductDetailTuner,
                THIABPurchaseOrder,
                THIABOrderId,
                THIABPurchase,
                THIABBillingRequestFull,
                IABException>
    implements THIABLogicHolder
{
    private IABSKUListCache iabskuListCache;
    private THIABProductDetailCache thskuDetailCache;

    protected IABPurchaseConsumerHolder<IABSKU, THIABOrderId, THIABPurchase, IABException> purchaseConsumerHolder;

    @Inject public THIABLogicHolderFull(UserProfileCache userProfileCache, UserServiceWrapper userServiceWrapper,
            HeroListCache heroListCache, IABSKUListCache iabskuListCache, THIABProductDetailCache thskuDetailCache)
    {
        super(userProfileCache, userServiceWrapper, heroListCache);
        this.iabskuListCache = iabskuListCache;
        this.thskuDetailCache = thskuDetailCache;
        purchaseConsumerHolder = createPurchaseConsumeHolder();
    }

     //<editor-fold desc="Life Cycle">
    @Override public void onDestroy()
    {
        purchaseConsumerHolder.onDestroy();
        super.onDestroy();
    }

    @Override protected THIABProductIdentifierFetcherHolder createProductIdentifierFetcherHolder()
    {
        return new THBaseIABProductIdentifierFetcherHolder();
    }

    @Override protected THIABInventoryFetcherHolder createInventoryFetcherHolder()
    {
        return new THBaseIABInventoryFetcherHolder();
    }

    @Override protected THIABPurchaseFetcherHolder createPurchaseFetcherHolder()
    {
        return new THBaseIABPurchaseFetcherHolder();
    }

    @Override protected THIABPurchaserHolder createPurchaserHolder()
    {
        return new THBaseIABPurchaserHolder();
    }

    @Override protected BillingAvailableTesterHolder<IABException> createBillingAvailableTesterHolder()
    {
        return new BaseIABBillingAvailableTesterHolder();
    }

    protected THIABPurchaseConsumerHolder createPurchaseConsumeHolder()
    {
        return new THBaseIABPurchaseConsumerHolder();
    }

    @Override protected THIABPurchaseReporterHolder createPurchaseReporterHolder()
    {
        return new THBaseIABPurchaseReporterHolder();
    }
    //</editor-fold>

    @Override public String getBillingHolderName(Resources resources)
    {
        return resources.getString(R.string.th_iab_logic_holder_name);
    }

    //<editor-fold desc="Request Code Management">
    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return super.isUnusedRequestCode(requestCode)
                && purchaseConsumerHolder.isUnusedRequestCode(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        super.forgetRequestCode(requestCode);
        purchaseConsumerHolder.forgetRequestCode(requestCode);
    }
    //</editor-fold>

    @Override public void registerListeners(int requestCode, THIABBillingRequestFull billingRequest)
    {
        super.registerListeners(requestCode, billingRequest);
        registerConsumptionFinishedListener(requestCode, billingRequest.consumptionFinishedListener);
    }

    @Override public List<THIABProductDetail> getDetailsOfDomain(ProductIdentifierDomain domain)
    {
        List<THIABProductDetail> details = thskuDetailCache.get(getAllSkus());
        if (details == null)
        {
            return null;
        }
        return ArrayUtils.filter(details, new THProductDetailDomainPredicate<IABSKU, THIABProductDetail>(domain));
    }

    protected BaseIABSKUList<IABSKU> getAllSkus()
    {
        BaseIABSKUList<IABSKU> mixed = iabskuListCache.get(IABSKUListKey.getInApp());
        BaseIABSKUList<IABSKU> subs = iabskuListCache.get(IABSKUListKey.getSubs());
        if (subs != null)
        {
            mixed.addAll(subs);
        }
        return mixed;
    }

    //<editor-fold desc="Run Logic">
    @Override protected boolean runInternal(int requestCode)
    {
        boolean launched = super.runInternal(requestCode);
        THIABBillingRequestFull billingRequest = billingRequests.get(requestCode);
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

    @Override protected boolean prepareToRestoreOnePurchase(int requestCode, THIABBillingRequestFull billingRequest)
    {
        boolean prepared = super.prepareToRestoreOnePurchase(requestCode, billingRequest);
        if (prepared && billingRequest != null)
        {
            billingRequest.consumePurchase = true;
        }
        return prepared;
    }
    //</editor-fold>

    //<editor-fold desc="Sequence Logic">
    @Override protected void prepareRequestForNextRunAfterPurchaseFetchedSuccess(int requestCode, List<THIABPurchase> purchases)
    {
        super.prepareRequestForNextRunAfterPurchaseFetchedSuccess(requestCode, purchases);
    }

    @Override protected void prepareRequestForNextRunAfterPurchaseFinished(int requestCode, THIABPurchaseOrder purchaseOrder, THIABPurchase purchase)
    {
        super.prepareRequestForNextRunAfterPurchaseFinished(requestCode, purchaseOrder, purchase);
        THIABBillingRequestFull billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.purchaseToReport = purchase;
        }
    }

    @Override protected void prepareRequestForNextRunAfterPurchaseReportedSuccess(int requestCode, THIABPurchase reportedPurchase, UserProfileDTO updatedUserPortfolio)
    {
        super.prepareRequestForNextRunAfterPurchaseReportedSuccess(requestCode, reportedPurchase, updatedUserPortfolio);
        THIABBillingRequestFull billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.purchaseToConsume = reportedPurchase;
        }
    }

    @Override protected void prepareRequestForNextRunAfterPurchaseReportedFailed(int requestCode, THIABPurchase reportedPurchase, IABException error)
    {
        super.prepareRequestForNextRunAfterPurchaseReportedFailed(requestCode, reportedPurchase, error);
        THIABBillingRequestFull billingRequest = billingRequests.get(requestCode);
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

    protected void handlePurchaseConsumed(int requestCode, THIABPurchase purchase)
    {
        notifyPurchaseConsumed(requestCode, purchase);
        prepareRequestForNextRunAfterPurchaseConsumed(requestCode, purchase);
        runInternal(requestCode);
    }

    protected void handlePurchaseNeedNotBeConsumed(int requestCode, THIABPurchase purchase)
    {
        prepareRequestForNextRunAfterPurchaseConsumed(requestCode, purchase);
        runInternal(requestCode);
    }

    protected void prepareRequestForNextRunAfterPurchaseConsumed(int requestCode, THIABPurchase purchase)
    {
        THIABBillingRequestFull billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.consumePurchase = false;
            if (billingRequest.restorePurchase)
            {
                if (purchase != null && purchase.getType().equals(IABConstants.ITEM_TYPE_INAPP))
                {
                    billingRequest.restoredPurchases.add(purchase);
                }
                prepareToRestoreOnePurchase(requestCode, billingRequest);
            }
        }
    }

    protected void handlePurchaseConsumedFailed(int requestCode, THIABPurchase purchase, IABException exception)
    {
        notifyPurchaseConsumedFailed(requestCode, purchase, exception);
        prepareRequestForNextRunAfterPurchaseConsumedFailed(requestCode, purchase, exception);
        runInternal(requestCode);
    }

    protected void prepareRequestForNextRunAfterPurchaseConsumedFailed(int requestCode, THIABPurchase purchase, IABException exception)
    {
        THIABBillingRequestFull billingRequest = billingRequests.get(requestCode);
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

    //<editor-fold desc="Fetch Product Identifier">
    @Override protected ProductIdentifierListCache<IABSKU, IABSKUListKey, IABSKUList> getProductIdentifierCache()
    {
        return iabskuListCache;
    }
    //</editor-fold>

    //<editor-fold desc="Fetch Inventory">
    @Override protected ProductDetailCache<IABSKU, THIABProductDetail, THIABProductDetailTuner> getProductDetailCache()
    {
        return thskuDetailCache;
    }
    //</editor-fold>

    //<editor-fold desc="Consume Purchase">
    @Override public IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKU, THIABOrderId, THIABPurchase, IABException> getConsumptionFinishedListener(int requestCode)
    {
        THIABBillingRequestFull billingRequest = billingRequests.get(requestCode);
        if (billingRequest == null)
        {
            return null;
        }
        return billingRequest.consumptionFinishedListener;
    }

    @Override public void registerConsumptionFinishedListener(int requestCode,
            IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKU, THIABOrderId, THIABPurchase, IABException> consumptionFinishedListener)
    {
        THIABBillingRequestFull billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.consumptionFinishedListener = consumptionFinishedListener;
            purchaseConsumerHolder.registerConsumptionFinishedListener(requestCode, createPurchaseConsumptionListener());
        }
    }

    protected IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKU, THIABOrderId, THIABPurchase, IABException> createPurchaseConsumptionListener()
    {
        return new IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKU, THIABOrderId, THIABPurchase, IABException>()
        {
            @Override public void onPurchaseConsumed(int requestCode, THIABPurchase purchase)
            {
                handlePurchaseConsumed(requestCode, purchase);
            }

            @Override public void onPurchaseConsumeFailed(int requestCode, THIABPurchase purchase, IABException exception)
            {
                handlePurchaseConsumedFailed(requestCode, purchase, exception);
            }
        };
    }

    @Override public void unregisterPurchaseConsumptionListener(int requestCode)
    {
        purchaseConsumerHolder.forgetRequestCode(requestCode);
        THIABBillingRequestFull billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.consumptionFinishedListener = null;
        }
    }

    protected void notifyPurchaseConsumed(int requestCode, THIABPurchase purchase)
    {
        IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKU, THIABOrderId, THIABPurchase, IABException> consumptionFinishedListener = getConsumptionFinishedListener(requestCode);
        if (consumptionFinishedListener != null)
        {
            consumptionFinishedListener.onPurchaseConsumed(requestCode, purchase);
        }
        unregisterPurchaseConsumptionListener(requestCode);
    }

    protected void notifyPurchaseConsumedFailed(int requestCode, THIABPurchase purchase, IABException exception)
    {
        IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKU, THIABOrderId, THIABPurchase, IABException> consumptionFinishedListener = getConsumptionFinishedListener(requestCode);
        if (consumptionFinishedListener != null)
        {
            consumptionFinishedListener.onPurchaseConsumeFailed(requestCode, purchase, exception);
        }
        unregisterPurchaseConsumptionListener(requestCode);
    }
    //</editor-fold>

    //<editor-fold desc="Launch Sequence Methods">
    @Override public void launchConsumeSequence(int requestCode, THIABPurchase purchase)
    {
        if (purchase != null
                && purchase.getType() != null
                && purchase.getType().equals(IABConstants.ITEM_TYPE_INAPP))
        {
            purchaseConsumerHolder.launchConsumeSequence(requestCode, purchase);
        }
        else
        {
            handlePurchaseNeedNotBeConsumed(requestCode, purchase);
        }
    }
    //</editor-fold>

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        ((IABPurchaserHolder<IABSKU, THIABPurchaseOrder, THIABOrderId, THIABPurchase, IABException>) purchaserHolder).onActivityResult(
                requestCode, resultCode, data);
    }
}
