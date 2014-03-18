package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.request.BillingRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    protected Map<Integer, BillingRequestType> billingRequests;

    protected Boolean billingAvailable = null;
    protected Map<Integer, OnBillingAvailableListener<BillingExceptionType>> billingAvailableListeners;

    protected ProductIdentifierFetcherHolder<ProductIdentifierType, BillingExceptionType> productIdentifierFetcherHolder;
    protected BillingInventoryFetcherHolder<ProductIdentifierType, ProductDetailType, BillingExceptionType> inventoryFetcherHolder;
    protected BillingPurchaseFetcherHolder<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseFetcherHolder;
    protected BillingPurchaserHolder<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaserHolder;

    public BaseBillingLogicHolder()
    {
        super();
        billingRequests = new HashMap<>();

        billingAvailableListeners = new HashMap<>();

        productIdentifierFetcherHolder = createProductIdentifierFetcherHolder();
        inventoryFetcherHolder = createInventoryFetcherHolder();
        purchaseFetcherHolder = createPurchaseFetcherHolder();
        purchaserHolder = createPurchaserHolder();

        testBillingAvailable();
    }

    abstract protected ProductIdentifierFetcherHolder<ProductIdentifierType, BillingExceptionType> createProductIdentifierFetcherHolder();
    abstract protected BillingInventoryFetcherHolder<ProductIdentifierType, ProductDetailType, BillingExceptionType> createInventoryFetcherHolder();
    abstract protected BillingPurchaseFetcherHolder<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> createPurchaseFetcherHolder();
    abstract protected BillingPurchaserHolder<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType, BillingExceptionType> createPurchaserHolder();

    @Override public void onDestroy()
    {
        for (BillingRequestType billingRequest : billingRequests.values())
        {
            if (billingRequest != null)
            {
                billingRequest.onDestroy();
            }
        }
        billingRequests.clear();

        billingAvailableListeners.clear();

        productIdentifierFetcherHolder.onDestroy();
        inventoryFetcherHolder.onDestroy();
        purchaseFetcherHolder.onDestroy();
        purchaserHolder.onDestroy();
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

    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return !billingRequests.containsKey(requestCode)
                && !billingAvailableListeners.containsKey(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        billingRequests.remove(requestCode);

        billingAvailableListeners.remove(requestCode);

        unregisterProductIdentifierFetchedListener(requestCode);
        unregisterInventoryFetchedListener(requestCode);
        unregisterPurchaseFetchedListener(requestCode);
        unregisterPurchaseFinishedListener(requestCode);
    }
    //</editor-fold>

    @Override public void registerListeners(int requestCode, BillingRequestType billingRequest)
    {
        registerBillingAvailableListener(requestCode, billingRequest.billingAvailableListener);
        registerProductIdentifierFetchedListener(requestCode, billingRequest.productIdentifierFetchedListener);
        registerInventoryFetchedListener(requestCode, billingRequest.inventoryFetchedListener);
        registerPurchaseFetchedListener(requestCode, billingRequest.purchaseFetchedListener);
        registerPurchaseFinishedListener(requestCode, billingRequest.purchaseFinishedListener);
    }

    /**
     *
     * @param requestCode
     * @param billingRequest
     * @return true if sequence launched, false otherwise
     */
    @Override public boolean run(int requestCode, BillingRequestType billingRequest)
    {
        registerListeners(requestCode, billingRequest);
        boolean launched = false;
        if (billingRequest != null && billingRequest.billingAvailable)
        {
            testBillingAvailable();
            launched = true;
        }
        return launched;
    }

    @Override public OnBillingAvailableListener<BillingExceptionType> getBillingAvailableListener(int requestCode)
    {
        return billingAvailableListeners.get(requestCode);
    }

    //<editor-fold desc="Launch Sequence Methods">
    @Override public void launchProductIdentifierFetchSequence(int requestCode)
    {
        productIdentifierFetcherHolder.launchProductIdentifierFetchSequence(requestCode);
    }

    @Override public void launchInventoryFetchSequence(int requestCode, List<ProductIdentifierType> allIds)
    {
        inventoryFetcherHolder.launchInventoryFetchSequence(requestCode, allIds);
    }

    @Override public void launchFetchPurchaseSequence(int requestCode)
    {
        purchaseFetcherHolder.launchFetchPurchaseSequence(requestCode);
    }

    @Override public void launchPurchaseSequence(int requestCode, PurchaseOrderType purchaseOrder)
    {
        purchaserHolder.launchPurchaseSequence(requestCode, purchaseOrder);
    }
    //</editor-fold>

    //<editor-fold desc="Sequence Logic">
    protected void handleProductIdentifierFetchedSuccess(int requestCode, Map<String, List<ProductIdentifierType>> availableProductIdentifiers)
    {
        notifyProductIdentifierFetchedSuccess(requestCode, availableProductIdentifiers);
        // TODO child class to continue with other sequence?
    }

    protected void handleInventoryFetchedSuccess(int requestCode, List<ProductIdentifierType> productIdentifiers, Map<ProductIdentifierType, ProductDetailType> inventory)
    {
        notifyInventoryFetchedSuccess(requestCode, productIdentifiers, inventory);
        // TODO continue another sequence?
    }

    protected void handlePurchaseFetchedSuccess(int requestCode, Map<ProductIdentifierType, ProductPurchaseType> purchases)
    {
        notifyPurchaseFetchedSuccess(requestCode, purchases);
        // TODO continue another sequence?
    }

    protected void handlePurchaseFinished(int requestCode, PurchaseOrderType purchaseOrder, ProductPurchaseType purchase)
    {
        notifyPurchaseFinished(requestCode, purchaseOrder, purchase);
        // TODO continue with other sequence?
    }
    //</editor-fold>


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
            availableListener.onBillingAvailable(requestCode);
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
            availableListener.onBillingNotAvailable(requestCode, exception);
        }
        unregisterBillingAvailableListener(requestCode);
    }
    //</editor-fold>

    //<editor-fold desc="Fetch Product Identifier">
    @Override
    public ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierType, BillingExceptionType> getProductIdentifierFetchedListener(int requestCode)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest == null)
        {
            return null;
        }
        return billingRequest.productIdentifierFetchedListener;
    }

    @Override public void registerProductIdentifierFetchedListener(int requestCode, ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierType, BillingExceptionType> productIdentifierFetchedListener)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.productIdentifierFetchedListener = productIdentifierFetchedListener;
            productIdentifierFetcherHolder.registerProductIdentifierFetchedListener(requestCode, createProductIdentifierFetchedListener());
        }
    }

    protected ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierType, BillingExceptionType> createProductIdentifierFetchedListener()
    {
        return new ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierType, BillingExceptionType>()
        {
            @Override public void onFetchedProductIdentifiers(int requestCode, Map<String, List<ProductIdentifierType>> availableProductIdentifiers)
            {
                handleProductIdentifierFetchedSuccess(requestCode, availableProductIdentifiers);
            }

            @Override public void onFetchProductIdentifiersFailed(int requestCode, BillingExceptionType exception)
            {
                notifyProductIdentifierFetchedFailed(requestCode, exception);
            }
        };
    }

    @Override public void unregisterProductIdentifierFetchedListener(int requestCode)
    {
        productIdentifierFetcherHolder.forgetRequestCode(requestCode);
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.productIdentifierFetchedListener = null;
        }
    }

    protected void notifyProductIdentifierFetchedSuccess(int requestCode, Map<String, List<ProductIdentifierType>> availableProductIdentifiers)
    {
        ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierType, BillingExceptionType> productIdentifierFetchedListener = getProductIdentifierFetchedListener(requestCode);
        if (productIdentifierFetchedListener != null)
        {
            productIdentifierFetchedListener.onFetchedProductIdentifiers(requestCode, availableProductIdentifiers);
        }
        unregisterProductIdentifierFetchedListener(requestCode);
    }

    protected void notifyProductIdentifierFetchedFailed(int requestCode, BillingExceptionType exception)
    {
        ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierType, BillingExceptionType> productIdentifierFetchedListener = getProductIdentifierFetchedListener(requestCode);
        if (productIdentifierFetchedListener != null)
        {
            productIdentifierFetchedListener.onFetchProductIdentifiersFailed(requestCode, exception);
        }
        unregisterProductIdentifierFetchedListener(requestCode);
    }
    //</editor-fold>

    //<editor-fold desc="Fetch Inventory">
    @Override public BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> getInventoryFetchedListener(int requestCode)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest == null)
        {
            return null;
        }
        return billingRequest.inventoryFetchedListener;
    }

    @Override public void registerInventoryFetchedListener(int requestCode, BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> inventoryFetchedListener)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.inventoryFetchedListener = inventoryFetchedListener;
            inventoryFetcherHolder.registerInventoryFetchedListener(requestCode, createInventoryFetchedListener());
        }
    }

    protected BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> createInventoryFetchedListener()
    {
        return new BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType>()
        {
            @Override public void onInventoryFetchSuccess(int requestCode, List<ProductIdentifierType> productIdentifiers, Map<ProductIdentifierType, ProductDetailType> inventory)
            {
                handleInventoryFetchedSuccess(requestCode, productIdentifiers, inventory);
            }

            @Override public void onInventoryFetchFail(int requestCode, List<ProductIdentifierType> productIdentifiers, BillingExceptionType exception)
            {
                notifyInventoryFetchFailed(requestCode, productIdentifiers, exception);
            }
        };
    }

    @Override public void unregisterInventoryFetchedListener(int requestCode)
    {
        inventoryFetcherHolder.forgetRequestCode(requestCode);
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.inventoryFetchedListener = null;
        }
    }

    protected void notifyInventoryFetchedSuccess(int requestCode, List<ProductIdentifierType> productIdentifiers, Map<ProductIdentifierType, ProductDetailType> inventory)
    {
        BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> inventoryFetchedListener = getInventoryFetchedListener(requestCode);
        if (inventoryFetchedListener != null)
        {
            inventoryFetchedListener.onInventoryFetchSuccess(requestCode, productIdentifiers, inventory);
        }
        unregisterInventoryFetchedListener(requestCode);
    }

    protected void notifyInventoryFetchFailed(int requestCode, List<ProductIdentifierType> productIdentifiers, BillingExceptionType exception)
    {
        BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> inventoryFetchedListener = getInventoryFetchedListener(requestCode);
        if (inventoryFetchedListener != null)
        {
            inventoryFetchedListener.onInventoryFetchFail(requestCode, productIdentifiers, exception);
        }
        unregisterInventoryFetchedListener(requestCode);
    }
    //</editor-fold>

    //<editor-fold desc="Fetch Purchase">
    @Override public BillingPurchaseFetcher.OnPurchaseFetchedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> getPurchaseFetchedListener(int requestCode)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest == null)
        {
            return null;
        }
        return billingRequest.purchaseFetchedListener;
    }

    @Override public void registerPurchaseFetchedListener(int requestCode, BillingPurchaseFetcher.OnPurchaseFetchedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseFetchedListener)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.purchaseFetchedListener = purchaseFetchedListener;
            purchaseFetcherHolder.registerPurchaseFetchedListener(requestCode, createPurchaseFetchedListener());
        }
    }

    protected BillingPurchaseFetcher.OnPurchaseFetchedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> createPurchaseFetchedListener()
    {
        return new BillingPurchaseFetcher.OnPurchaseFetchedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType>()
        {
            @Override public void onFetchedPurchases(int requestCode, Map<ProductIdentifierType, ProductPurchaseType> purchases)
            {
                handlePurchaseFetchedSuccess(requestCode, purchases);
            }

            @Override public void onFetchPurchasesFailed(int requestCode, BillingExceptionType exception)
            {
                notifyPurchaseFetchedFailed(requestCode, exception);
            }
        };
    }

    @Override public void unregisterPurchaseFetchedListener(int requestCode)
    {
        purchaseFetcherHolder.forgetRequestCode(requestCode);
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.purchaseFetchedListener = null;
        }
    }

    protected void notifyPurchaseFetchedSuccess(int requestCode, Map<ProductIdentifierType, ProductPurchaseType> purchases)
    {
        BillingPurchaseFetcher.OnPurchaseFetchedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseFetchedListener = getPurchaseFetchedListener(requestCode);
        if (purchaseFetchedListener != null)
        {
            purchaseFetchedListener.onFetchedPurchases(requestCode, purchases);
        }
        unregisterPurchaseFetchedListener(requestCode);
    }

    protected void notifyPurchaseFetchedFailed(int requestCode, BillingExceptionType exception)
    {
        BillingPurchaseFetcher.OnPurchaseFetchedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseFetchedListener = getPurchaseFetchedListener(requestCode);
        if (purchaseFetchedListener != null)
        {
            purchaseFetchedListener.onFetchPurchasesFailed(requestCode, exception);
        }
        unregisterPurchaseFetchedListener(requestCode);
    }
    //</editor-fold>

    //<editor-fold desc="Purchasing">
    @Override public BillingPurchaser.OnPurchaseFinishedListener<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType, BillingExceptionType> getPurchaseFinishedListener(int requestCode)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest == null)
        {
            return null;
        }
        return billingRequest.purchaseFinishedListener;
    }

    @Override public void registerPurchaseFinishedListener(int requestCode, BillingPurchaser.OnPurchaseFinishedListener<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseFinishedListener)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.purchaseFinishedListener = purchaseFinishedListener;
            purchaserHolder.registerPurchaseFinishedListener(requestCode, createPurchaseFinishedListener());
        }
    }

    protected BillingPurchaser.OnPurchaseFinishedListener<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType, BillingExceptionType> createPurchaseFinishedListener()
    {
        return new BillingPurchaser.OnPurchaseFinishedListener<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType, BillingExceptionType>()
        {
            @Override public void onPurchaseFinished(int requestCode, PurchaseOrderType purchaseOrder, ProductPurchaseType purchase)
            {
                handlePurchaseFinished(requestCode, purchaseOrder, purchase);
            }

            @Override public void onPurchaseFailed(int requestCode, PurchaseOrderType purchaseOrder, BillingExceptionType exception)
            {
                notifyPurchaseFailed(requestCode, purchaseOrder, exception);
            }
        };
    }

    @Override public void unregisterPurchaseFinishedListener(int requestCode)
    {
        purchaserHolder.forgetRequestCode(requestCode);
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.purchaseFinishedListener = null;
        }
    }

    protected void notifyPurchaseFinished(int requestCode, PurchaseOrderType purchaseOrder, ProductPurchaseType purchase)
    {
        BillingPurchaser.OnPurchaseFinishedListener<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseFinishedListener = getPurchaseFinishedListener(requestCode);
        if (purchaseFinishedListener != null)
        {
            purchaseFinishedListener.onPurchaseFinished(requestCode, purchaseOrder, purchase);
        }
        unregisterPurchaseFinishedListener(requestCode);
    }

    protected void notifyPurchaseFailed(int requestCode, PurchaseOrderType purchaseOrder, BillingExceptionType billingException)
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
