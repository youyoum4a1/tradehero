package com.tradehero.th.billing.googleplay;

import android.content.Intent;
import android.content.res.Resources;
import com.tradehero.common.billing.BillingAvailableTesterHolder;
import com.tradehero.common.billing.googleplay.BaseIABBillingAvailableTesterHolder;
import com.tradehero.common.billing.googleplay.BaseIABSKUList;
import com.tradehero.common.billing.googleplay.IABConstants;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumerHolder;
import com.tradehero.common.billing.googleplay.IABPurchaserHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUListType;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.utils.ArrayUtils;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THBaseBillingLogicHolder;
import com.tradehero.th.billing.googleplay.request.THIABBillingRequestFull;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.billing.googleplay.IABSKUListCache;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 12:32 PM To change this template use File | Settings | File Templates. */
public class THIABLogicHolderFull
    extends THBaseBillingLogicHolder<
                IABSKU,
                THIABProductDetail,
                THIABPurchaseOrder,
                THIABOrderId,
                THIABPurchase,
                THIABBillingRequestFull,
                IABException>
    implements THIABLogicHolder
{
    public static final String TAG = THIABLogicHolderFull.class.getSimpleName();

    private IABSKUListCache iabskuListCache;
    private THIABProductDetailCache thskuDetailCache;

    protected IABPurchaseConsumerHolder<IABSKU, THIABOrderId, THIABPurchase, IABException> purchaseConsumerHolder;

    @Inject public THIABLogicHolderFull(UserProfileCache userProfileCache, UserServiceWrapper userServiceWrapper, IABSKUListCache iabskuListCache, THIABProductDetailCache thskuDetailCache)
    {
        super(userProfileCache, userServiceWrapper);
        this.iabskuListCache = iabskuListCache;
        this.thskuDetailCache = thskuDetailCache;
        purchaseConsumerHolder = createPurchaseConsumeHolder();
    }

    @Override public void onDestroy()
    {
        purchaseConsumerHolder.onDestroy();
        super.onDestroy();
    }

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

    @Override public List<THIABProductDetail> getDetailsOfDomain(ProductIdentifierDomain domain)
    {
        return ArrayUtils.filter(thskuDetailCache.get(getAllSkus()),
                THIABProductDetail.getPredicateIsOfCertainDomain(domain));
    }

    protected BaseIABSKUList<IABSKU> getAllSkus()
    {
        BaseIABSKUList<IABSKU> mixed = iabskuListCache.get(IABSKUListType.getInApp());
        BaseIABSKUList<IABSKU> subs = iabskuListCache.get(IABSKUListType.getSubs());
        if (subs != null)
        {
            mixed.addAll(subs);
        }
        return mixed;
    }

    //<editor-fold desc="Holder Creation">
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

    //<editor-fold desc="Sequence Logic">
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
        }
        return launched;
    }

    @Override protected void prepareRequestForNextRunAfterPurchaseFetchedSuccess(int requestCode, Map<IABSKU, THIABPurchase> purchases)
    {
        super.prepareRequestForNextRunAfterPurchaseFetchedSuccess(requestCode, purchases);
        // TODO restore?
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

    protected void handlePurchaseConsumed(int requestCode, THIABPurchase purchase)
    {
        notifyPurchaseConsumed(requestCode, purchase);
        THIABBillingRequestFull billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.consumePurchase = false;
        }
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
                notifyPurchaseConsumedFailed(requestCode, purchase, exception);
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
                && !purchase.getType().equals(IABConstants.ITEM_TYPE_INAPP))
        {
            purchaseConsumerHolder.launchConsumeSequence(requestCode, purchase);
        }
        else
        {
            handlePurchaseConsumed(requestCode, purchase);
        }
    }
    //</editor-fold>

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        ((IABPurchaserHolder<IABSKU, THIABPurchaseOrder, THIABOrderId, THIABPurchase, IABException>) purchaserHolder).onActivityResult(
                requestCode, resultCode, data);
    }

    @Deprecated
    @Override public boolean isInventoryReady()
    {
        return inventoryFetcherHolder.isInventoryReady();
    }

    @Deprecated
    @Override public boolean hadErrorLoadingInventory()
    {
        return inventoryFetcherHolder.hadErrorLoadingInventory();
    }
}
