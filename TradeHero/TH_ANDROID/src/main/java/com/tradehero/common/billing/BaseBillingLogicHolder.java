package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import timber.log.Timber;

/**
 * Created by xavier on 2/24/14.
 */
abstract public class BaseBillingLogicHolder<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingRequestType extends BillingRequest<
                ProductIdentifierType,
                ProductDetailType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType>,
        BillingExceptionType extends BillingException>
    implements BillingLogicHolder<
        ProductIdentifierType,
        ProductDetailType,
        PurchaseOrderType,
        OrderIdType,
        ProductPurchaseType,
        BillingRequestType,
        BillingExceptionType>
{
    public static final int MAX_RANDOM_RETRIES = 50;

    protected Boolean billingAvailable = null;
    protected Map<Integer, OnBillingAvailableListener<BillingExceptionType>> billingAvailableListeners;

    public BaseBillingLogicHolder()
    {
        super();
        billingAvailableListeners = new HashMap<>();
        testBillingAvailable();
    }

    @Override public void onDestroy()
    {
        if (billingAvailableListeners != null)
        {
            billingAvailableListeners.clear();
        }
    }

    //<editor-fold desc="Request Code Management">
    @Override public int getUnusedRequestCode()
    {
        int retries = MAX_RANDOM_RETRIES;
        int randomNumber;
        while (retries-- > 0)
        {
            randomNumber = (int) (Math.random() * Integer.MAX_VALUE);
            if (isUnusedRequestCode(randomNumber))
            {
                return randomNumber;
            }
        }
        throw new IllegalStateException("Could not find an unused requestCode after " + MAX_RANDOM_RETRIES + " trials");
    }

    @Override public boolean isUnusedRequestCode(int randomNumber)
    {
        return !billingAvailableListeners.containsKey(randomNumber);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        billingAvailableListeners.remove(requestCode);
    }
    //</editor-fold>

    @Override public void registerListeners(int requestCode, BillingRequestType billingRequest)
    {
        registerBillingAvailableListener(requestCode, billingRequest.getBillingAvailableListener());
        registerProductIdentifierFetchedListener(requestCode, billingRequest.getProductIdentifierFetchedListener());
        registerInventoryFetchedListener(requestCode, billingRequest.getInventoryFetchedListener());
        registerPurchaseFetchedListener(requestCode, billingRequest.getPurchaseFetchedListener());
        registerPurchaseFinishedListener(requestCode, billingRequest.getPurchaseFinishedListener());
    }

    @Override public void run(int requestCode, BillingRequestType billingRequest)
    {
        registerListeners(requestCode, billingRequest);
        // TODO more
    }

    @Override public OnBillingAvailableListener<BillingExceptionType> getBillingAvailableListener(int requestCode)
    {
        return billingAvailableListeners.get(requestCode);
    }

    //<editor-fold desc="Billing Available">
    @Override public Boolean isBillingAvailable()
    {
        return billingAvailable;
    }

    abstract protected void testBillingAvailable();

    @Override public void registerBillingAvailableListener(int requestCode,
            OnBillingAvailableListener<BillingExceptionType> billingAvailableListener)
    {
        billingAvailableListeners.put(requestCode, billingAvailableListener);
    }

    @Override public void unregisterBillingAvailableListener(int requestCode)
    {
        billingAvailableListeners.remove(requestCode);
    }

    protected void notifyBillingAvailable()
    {
        billingAvailable = true;
        // Protect from unsync when unregistering the listeners
        for (Integer requestCode : new ArrayList<>(billingAvailableListeners.keySet()))
        {
            notifyBillingAvailable(requestCode);
        }
    }

    protected void notifyBillingAvailable(int requestCode)
    {
        OnBillingAvailableListener<BillingExceptionType> availableListener = billingAvailableListeners.get(requestCode);
        if (availableListener != null)
        {
            availableListener.onBillingAvailable();
        }
        unregisterBillingAvailableListener(requestCode);
    }

    protected void notifyBillingNotAvailable(BillingExceptionType exception)
    {
        billingAvailable = false;
        // Protect from unsync when unregistering the listeners
        for (Integer requestCode : new ArrayList<>(billingAvailableListeners.keySet()))
        {
            notifyBillingNotAvailable(requestCode, exception);
        }
    }

    protected void notifyBillingNotAvailable(int requestCode, BillingExceptionType exception)
    {
        OnBillingAvailableListener<BillingExceptionType> availableListener = billingAvailableListeners.get(requestCode);
        if (availableListener != null)
        {
            availableListener.onBillingNotAvailable(exception);
        }
        unregisterBillingAvailableListener(requestCode);
    }
    //</editor-fold>

    //<editor-fold desc="Notify Product Identifier">
    protected void notifyFetchedProductIdentifiers(int requestCode, Map<String, List<ProductIdentifierType>> availableProductIdentifiers)
    {
        ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierType, BillingExceptionType> productIdentifierFetchedListener = getProductIdentifierFetchedListener(requestCode);
        if (productIdentifierFetchedListener != null)
        {
            productIdentifierFetchedListener.onFetchedProductIdentifiers(requestCode, availableProductIdentifiers);
        }
        unregisterProductIdentifierFetchedListener(requestCode);
    }

    protected void notifyFetchProductIdentifiersFailed(int requestCode, BillingExceptionType exception)
    {
        ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierType, BillingExceptionType> productIdentifierFetchedListener = getProductIdentifierFetchedListener(requestCode);
        if (productIdentifierFetchedListener != null)
        {
            productIdentifierFetchedListener.onFetchProductIdentifiersFailed(requestCode, exception);
        }
        unregisterProductIdentifierFetchedListener(requestCode);
    }
    //</editor-fold>

    //<editor-fold desc="Notify Inventory Fetched">
    protected void notifyInventoryFetchSuccess(
            int requestCode,
            List<ProductIdentifierType> productIdentifiers,
            Map<ProductIdentifierType, ProductDetailType> inventory)
    {
        BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> inventoryFetchedListener = getInventoryFetchedListener(requestCode);
        if (inventoryFetchedListener != null)
        {
            inventoryFetchedListener.onInventoryFetchSuccess(requestCode, productIdentifiers, inventory);
        }
        unregisterInventoryFetchedListener(requestCode);
    }

    protected void notifyInventoryFetchFail(
            int requestCode,
            List<ProductIdentifierType> productIdentifiers,
            BillingExceptionType exception)
    {
        BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> inventoryFetchedListener = getInventoryFetchedListener(requestCode);
        if (inventoryFetchedListener != null)
        {
            inventoryFetchedListener.onInventoryFetchFail(requestCode, productIdentifiers, exception);
        }
        unregisterInventoryFetchedListener(requestCode);
    }
    //</editor-fold>

    //<editor-fold desc="Notify Purchase Fetched">
    protected void notifyFetchedPurchases(int requestCode, Map<ProductIdentifierType, ProductPurchaseType> purchases)
    {
        BillingPurchaseFetcher.OnPurchaseFetchedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseFetchedListener = getPurchaseFetchedListener(requestCode);
        if (purchaseFetchedListener != null)
        {
            purchaseFetchedListener.onFetchedPurchases(requestCode, purchases);
        }
        unregisterPurchaseFetchedListener(requestCode);
    }

    protected void notifyFetchPurchasesFailed(int requestCode, BillingExceptionType exception)
    {
        BillingPurchaseFetcher.OnPurchaseFetchedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseFetchedListener = getPurchaseFetchedListener(requestCode);
        if (purchaseFetchedListener != null)
        {
            purchaseFetchedListener.onFetchPurchasesFailed(requestCode, exception);
        }
        unregisterPurchaseFetchedListener(requestCode);
    }
    //</editor-fold>

    //<editor-fold desc="Notify Purchase Finished">
    protected void onPurchaseFinished(int requestCode, PurchaseOrderType purchaseOrder, ProductPurchaseType purchase)
    {
        BillingPurchaser.OnPurchaseFinishedListener<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseFinishedListener = getPurchaseFinishedListener(requestCode);
        if (purchaseFinishedListener != null)
        {
            purchaseFinishedListener.onPurchaseFinished(requestCode, purchaseOrder, purchase);
        }
        unregisterPurchaseFinishedListener(requestCode);
    }

    protected void onPurchaseFailed(int requestCode, PurchaseOrderType purchaseOrder, BillingExceptionType billingException)
    {
        BillingPurchaser.OnPurchaseFinishedListener<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseFinishedListener = getPurchaseFinishedListener(requestCode);
        if (purchaseFinishedListener != null)
        {
            purchaseFinishedListener.onPurchaseFailed(requestCode, purchaseOrder, billingException);
        }
        unregisterPurchaseFinishedListener(requestCode);
    }
    //</editor-fold>
}
