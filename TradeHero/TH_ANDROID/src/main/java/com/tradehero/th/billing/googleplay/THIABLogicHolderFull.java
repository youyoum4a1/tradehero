package com.tradehero.th.billing.googleplay;

import android.content.Intent;
import android.content.res.Resources;
import com.tradehero.common.billing.googleplay.BaseIABSKUList;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumerHolder;
import com.tradehero.common.billing.googleplay.IABPurchaserHolder;
import com.tradehero.common.billing.googleplay.IABResponse;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUListType;
import com.tradehero.common.billing.googleplay.IABServiceConnector;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.utils.ArrayUtils;
import com.tradehero.th.R;
import com.tradehero.th.billing.THBaseBillingLogicHolder;
import com.tradehero.th.persistence.billing.googleplay.IABSKUListCache;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.util.List;
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

    @Inject protected Lazy<IABSKUListCache> iabskuListCache;
    @Inject protected Lazy<THIABProductDetailCache> thskuDetailCache;

    protected IABServiceConnector availabilityTester;
    protected IABPurchaseConsumerHolder<IABSKU, THIABOrderId, THIABPurchase, IABException> purchaseConsumerHolder;

    public THIABLogicHolderFull()
    {
        super();
        purchaseConsumerHolder = createPurchaseConsumeHolder();
        DaggerUtils.inject(this);
    }

    @Override public void onDestroy()
    {
        if (availabilityTester != null)
        {
            availabilityTester.onDestroy();
        }
        purchaseConsumerHolder.onDestroy();
        super.onDestroy();
    }

    @Override public String getBillingHolderName(Resources resources)
    {
        return resources.getString(R.string.th_iab_logic_holder_name);
    }

    @Override protected void testBillingAvailable()
    {
        availabilityTester = new AvailabilityTester();
        availabilityTester.startConnectionSetup();
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

    @Override public List<THIABProductDetail> getDetailsOfDomain(String domain)
    {
        return ArrayUtils.filter(thskuDetailCache.get().get(getAllSkus()),
                THIABProductDetail.getPredicateIsOfCertainDomain(domain));
    }

    protected BaseIABSKUList<IABSKU> getAllSkus()
    {
        BaseIABSKUList<IABSKU> mixed = iabskuListCache.get().get(IABSKUListType.getInApp());
        BaseIABSKUList<IABSKU> subs = iabskuListCache.get().get(IABSKUListType.getSubs());
        if (subs != null)
        {
            mixed.addAll(subs);
        }
        return mixed;
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

    protected THIABPurchaseConsumerHolder createPurchaseConsumeHolder()
    {
        return new THBaseIABPurchaseConsumerHolder();
    }

    protected THIABPurchaseReporterHolder createPurchaseReporterHolder()
    {
        return new THBaseIABPurchaseReporterHolder();
    }

    //<editor-fold desc="Consume Purchase">
    @Override public IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKU, THIABOrderId, THIABPurchase, IABException> getConsumptionFinishedListener(int requestCode)
    {
        THIABBillingRequestFull billingRequest = billingRequests.get(requestCode);
        if (billingRequest == null)
        {
            return null;
        }
        return billingRequest.getConsumptionFinishedListener();
    }

    @Override public void registerConsumptionFinishedListener(int requestCode,
            IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKU, THIABOrderId, THIABPurchase, IABException> consumptionFinishedListener)
    {
        THIABBillingRequestFull billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.setConsumptionFinishedListener(consumptionFinishedListener);
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
            billingRequest.setConsumptionFinishedListener(null);
        }
    }

    protected void handlePurchaseConsumed(int requestCode, THIABPurchase purchase)
    {
        notifyPurchaseConsumed(requestCode, purchase);
        // TODO further sequence?
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
    @Override public void launchProductIdentifierFetchSequence(int requestCode)
    {
        productIdentifierFetcherHolder.launchProductIdentifierFetchSequence(requestCode);
    }

    @Override public void launchInventoryFetchSequence(int requestCode, List<IABSKU> allIds)
    {
        inventoryFetcherHolder.launchInventoryFetchSequence(requestCode, allIds);
    }

    @Override public void launchFetchPurchaseSequence(int requestCode)
    {
        purchaseFetcherHolder.launchFetchPurchaseSequence(requestCode);
    }

    @Override public void launchPurchaseSequence(int requestCode, THIABPurchaseOrder purchaseOrder)
    {
        purchaserHolder.launchPurchaseSequence(requestCode, purchaseOrder);
    }

    @Override public void launchConsumeSequence(int requestCode, THIABPurchase purchase)
    {
        purchaseConsumerHolder.launchConsumeSequence(requestCode, purchase);
    }
    //</editor-fold>

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        ((IABPurchaserHolder<IABSKU, THIABPurchaseOrder, THIABOrderId, THIABPurchase, IABException>) purchaserHolder).onActivityResult(
                requestCode, resultCode, data);
    }

    @Override public boolean isInventoryReady()
    {
        return inventoryFetcherHolder.isInventoryReady();
    }

    @Override public boolean hadErrorLoadingInventory()
    {
        return inventoryFetcherHolder.hadErrorLoadingInventory();
    }

    public class AvailabilityTester extends IABServiceConnector
    {
        protected AvailabilityTester()
        {
            super();
        }

        @Override protected void handleSetupFinished(IABResponse response)
        {
            super.handleSetupFinished(response);
            notifyBillingAvailable();
        }

        @Override protected void handleSetupFailed(IABException exception)
        {
            super.handleSetupFailed(exception);
            notifyBillingNotAvailable(exception);
        }
    }
}
