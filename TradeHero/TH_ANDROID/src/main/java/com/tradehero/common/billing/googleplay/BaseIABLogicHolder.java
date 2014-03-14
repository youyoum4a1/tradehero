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
        IABProductIdentifierFetcherHolderType extends ProductIdentifierFetcherHolder<
                IABSKUType,
                IABException>,
        IABProductDetailType extends IABProductDetail<IABSKUType>,
        IABInventoryFetcherHolderType extends IABInventoryFetcherHolder<
                IABSKUType,
                IABProductDetailType,
                IABException>,
        IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<
                IABSKUType,
                IABOrderIdType>,
        IABPurchaseFetcherHolderType extends IABPurchaseFetcherHolder<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType,
                IABException>,
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
    protected IABProductIdentifierFetcherHolderType productIdentifierFetcherHolder;
    protected IABInventoryFetcherHolderType inventoryFetcherHolder;
    protected IABPurchaseFetcherHolderType purchaseFetcherHolder;
    protected IABPurchaserHolderType purchaserHolder;
    protected IABPurchaseConsumerHolderType purchaseConsumerHolder;

    public BaseIABLogicHolder()
    {
        super();
        productIdentifierFetcherHolder = createProductIdentifierFetcherHolder();
        inventoryFetcherHolder = createInventoryFetcherHolder();
        purchaseFetcherHolder = createPurchaseFetcherHolder();
        purchaserHolder = createPurchaserHolder();
        purchaseConsumerHolder = createPurchaseConsumeHolder();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
        if (availabilityTester != null)
        {
            availabilityTester.onDestroy();
        }

        if (productIdentifierFetcherHolder != null)
        {
            productIdentifierFetcherHolder.onDestroy();
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
    abstract protected IABProductIdentifierFetcherHolderType createProductIdentifierFetcherHolder();
    abstract protected IABInventoryFetcherHolderType createInventoryFetcherHolder();
    abstract protected IABPurchaseFetcherHolderType createPurchaseFetcherHolder();
    abstract protected IABPurchaserHolderType createPurchaserHolder();
    abstract protected IABPurchaseConsumerHolderType createPurchaseConsumeHolder();

    //<editor-fold desc="Get Listener Methods">
    @Override
    public ProductIdentifierFetcher.OnProductIdentifierFetchedListener<IABSKUType, IABException> getProductIdentifierFetchedListener(int requestCode)
    {
        return productIdentifierFetcherHolder.getProductIdentifierFetchedListener(requestCode);
    }

    @Override public BillingInventoryFetcher.OnInventoryFetchedListener<IABSKUType, IABProductDetailType, IABException> getInventoryFetchedListener(
            int requestCode)
    {
        return inventoryFetcherHolder.getInventoryFetchedListener(requestCode);
    }

    @Override
    public BillingPurchaseFetcher.OnPurchaseFetchedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException> getPurchaseFetchedListener(
            int requestCode)
    {
        return purchaseFetcherHolder.getPurchaseFetchedListener(requestCode);
    }

    @Override
    public BillingPurchaser.OnPurchaseFinishedListener<IABSKUType, IABPurchaseOrderType, IABOrderIdType, IABPurchaseType, IABException> getPurchaseFinishedListener(
            int requestCode)
    {
        return purchaserHolder.getPurchaseFinishedListener(requestCode);
    }

    @Override
    public IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException> getConsumptionFinishedListener(
            int requestCode)
    {
        return purchaseConsumerHolder.getConsumptionFinishedListener(requestCode);
    }
    //</editor-fold>

    //<editor-fold desc="Register Listener Methods">
    @Override public void registerProductIdentifierFetchedListener(int requestCode,
            ProductIdentifierFetcher.OnProductIdentifierFetchedListener<IABSKUType, IABException> productIdentifierFetchedListener)
    {
        productIdentifierFetcherHolder.registerProductIdentifierFetchedListener(requestCode, productIdentifierFetchedListener);
    }

    @Override public void registerInventoryFetchedListener(int requestCode,
            BillingInventoryFetcher.OnInventoryFetchedListener<IABSKUType, IABProductDetailType, IABException> inventoryFetchedListener)
    {
        inventoryFetcherHolder.registerInventoryFetchedListener(requestCode, inventoryFetchedListener);
    }

    @Override public void registerPurchaseFetchedListener(int requestCode,
            BillingPurchaseFetcher.OnPurchaseFetchedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException> purchaseFetchedListener)
    {
        purchaseFetcherHolder.registerPurchaseFetchedListener(requestCode, purchaseFetchedListener);
    }

    @Override public void registerPurchaseFinishedListener(int requestCode,
            BillingPurchaser.OnPurchaseFinishedListener<IABSKUType, IABPurchaseOrderType, IABOrderIdType, IABPurchaseType, IABException> purchaseFinishedListener)
    {
        purchaserHolder.registerPurchaseFinishedListener(requestCode, purchaseFinishedListener);
    }

    @Override public void registerConsumptionFinishedListener(int requestCode,
            IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException> consumptionFinishedListener)
    {
        purchaseConsumerHolder.registerConsumptionFinishedListener(requestCode, consumptionFinishedListener);
    }
    //</editor-fold>

    //<editor-fold desc="Unregister Listener Methods">
    @Override public void unregisterProductIdentifierFetchedListener(int requestCode)
    {
        productIdentifierFetcherHolder.forgetRequestCode(requestCode);
    }

    @Override public void unregisterInventoryFetchedListener(int requestCode)
    {
        inventoryFetcherHolder.forgetRequestCode(requestCode);
    }

    @Override public void unregisterPurchaseFetchedListener(int requestCode)
    {
        purchaseFetcherHolder.forgetRequestCode(requestCode);
    }

    @Override public void unregisterPurchaseFinishedListener(int requestCode)
    {
        purchaserHolder.forgetRequestCode(requestCode);
    }
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
        purchaserHolder.onActivityResult(requestCode, resultCode, data);
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
