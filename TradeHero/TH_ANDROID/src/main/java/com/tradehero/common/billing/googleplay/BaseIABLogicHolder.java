package com.tradehero.common.billing.googleplay;

import android.content.Intent;
import com.tradehero.common.billing.BaseBillingLogicHolder;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.BillingRequest;
import com.tradehero.common.billing.ProductIdentifierFetcher;
import com.tradehero.common.billing.ProductIdentifierFetcherHolder;
import com.tradehero.common.billing.googleplay.exception.IABException;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 12:32 PM To change this template use File | Settings | File Templates. */
abstract public class BaseIABLogicHolder<
        IABSKUType extends IABSKU,
        IABProductDetailType extends IABProductDetail<IABSKUType>,
        IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<
                IABSKUType,
                IABOrderIdType>,
        IABPurchaserHolderType extends IABPurchaserHolder<
                IABSKUType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType,
                IABException>,
        IABPurchaseConsumerHolderType extends IABPurchaseConsumerHolder<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType,
                IABException>,
        BillingRequestType extends BillingRequest<
                IABSKUType,
                IABProductDetailType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType,
                IABException>>
    extends BaseBillingLogicHolder<
        IABSKUType,
        IABProductDetailType,
        IABPurchaseOrderType,
        IABOrderIdType,
        IABPurchaseType,
        BillingRequestType,
        IABException>
    implements IABLogicHolder<
            IABSKUType,
            IABProductDetailType,
            IABPurchaseOrderType,
            IABOrderIdType,
            IABPurchaseType,
            BillingRequestType,
            IABException>
{
    protected IABServiceConnector availabilityTester;
    protected IABPurchaseConsumerHolderType purchaseConsumerHolder;

    public BaseIABLogicHolder()
    {
        super();

        purchaseConsumerHolder = createPurchaseConsumeHolder();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
        if (availabilityTester != null)
        {
            availabilityTester.onDestroy();
        }

        if (inventoryFetcherHolder != null)
        {
            inventoryFetcherHolder.onDestroy();
        }

        if (purchaserHolder != null)
        {
            purchaserHolder.onDestroy();
        }

        if (purchaseConsumerHolder != null)
        {
            purchaseConsumerHolder.onDestroy();
        }

        if (availabilityTester != null)
        {
            availabilityTester.onDestroy();
        }
    }

    @Override protected void testBillingAvailable()
    {
        availabilityTester = new AvailabilityTester();
        availabilityTester.startConnectionSetup();
    }

    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return
                productIdentifierFetcherHolder.isUnusedRequestCode(requestCode) &&
                inventoryFetcherHolder.isUnusedRequestCode(requestCode) &&
                purchaseFetcherHolder.isUnusedRequestCode(requestCode) &&
                purchaserHolder.isUnusedRequestCode(requestCode) &&
                purchaseConsumerHolder.isUnusedRequestCode(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        super.forgetRequestCode(requestCode);
        productIdentifierFetcherHolder.forgetRequestCode(requestCode);
        inventoryFetcherHolder.forgetRequestCode(requestCode);
        purchaseFetcherHolder.forgetRequestCode(requestCode);
        purchaserHolder.forgetRequestCode(requestCode);
        purchaseConsumerHolder.forgetRequestCode(requestCode);
    }

    abstract protected BaseIABSKUList<IABSKUType> getAllSkus();
    abstract protected IABPurchaseConsumerHolderType createPurchaseConsumeHolder();

    //<editor-fold desc="Get Listener Methods">
    @Override
    public IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException> getConsumptionFinishedListener(
            int requestCode)
    {
        return purchaseConsumerHolder.getConsumptionFinishedListener(requestCode);
    }
    //</editor-fold>

    //<editor-fold desc="Register Listener Methods">
    @Override public void registerConsumptionFinishedListener(int requestCode,
            IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException> consumptionFinishedListener)
    {
        purchaseConsumerHolder.registerConsumptionFinishedListener(requestCode, consumptionFinishedListener);
    }
    //</editor-fold>

    //<editor-fold desc="Unregister Listener Methods">

    //</editor-fold>

    //<editor-fold desc="Launch Sequence Methods">
    @Override public void launchProductIdentifierFetchSequence(int requestCode)
    {
        productIdentifierFetcherHolder.launchProductIdentifierFetchSequence(requestCode);
    }

    @Override public void launchInventoryFetchSequence(int requestCode, List<IABSKUType> allIds)
    {
        inventoryFetcherHolder.launchInventoryFetchSequence(requestCode, allIds);
    }

    @Override public void launchFetchPurchaseSequence(int requestCode)
    {
        purchaseFetcherHolder.launchFetchPurchaseSequence(requestCode);
    }

    @Override public void launchPurchaseSequence(int requestCode, IABPurchaseOrderType purchaseOrder)
    {
        purchaserHolder.launchPurchaseSequence(requestCode, purchaseOrder);
    }

    @Override public void launchConsumeSequence(int requestCode, IABPurchaseType purchase)
    {
        purchaseConsumerHolder.launchConsumeSequence(requestCode, purchase);
    }
    //</editor-fold>

    //<editor-fold desc="Notify Listener Methods">
    protected void notifyPurchaseConsumed(int requestCode, IABPurchaseType purchase)
    {
        IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException> consumptionFinishedListener = getConsumptionFinishedListener(requestCode);
        if (consumptionFinishedListener != null)
        {
            consumptionFinishedListener.onPurchaseConsumed(requestCode, purchase);
        }
    }

    protected void notifyPurchaseConsumeFailed(int requestCode, IABPurchaseType purchase, IABException exception)
    {
        IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException> consumptionFinishedListener = getConsumptionFinishedListener(requestCode);
        if (consumptionFinishedListener != null)
        {
            consumptionFinishedListener.onPurchaseConsumeFailed(requestCode, purchase, exception);
        }
    }
    //</editor-fold>

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        ((IABPurchaserHolder<IABSKUType, IABPurchaseOrderType, IABOrderIdType, IABPurchaseType, IABException>) purchaserHolder).onActivityResult(
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
