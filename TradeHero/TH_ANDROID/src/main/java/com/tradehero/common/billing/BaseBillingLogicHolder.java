package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;
<<<<<<< HEAD
import java.util.ArrayList;
=======
import com.tradehero.common.billing.request.BillingRequest;
>>>>>>> Introduced a billing available tester holder to mimic other holders.
import java.util.HashMap;
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

<<<<<<< HEAD
    protected Boolean billingAvailable = null;
    protected Map<Integer, OnBillingAvailableListener<BillingExceptionType>> billingAvailableListeners;
=======
    protected Map<Integer, BillingRequestType> billingRequests;

    protected BillingAvailableTesterHolder<BillingExceptionType> billingAvailableTesterHolder;
    protected ProductIdentifierFetcherHolder<ProductIdentifierType, BillingExceptionType> productIdentifierFetcherHolder;
    protected BillingInventoryFetcherHolder<ProductIdentifierType, ProductDetailType, BillingExceptionType> inventoryFetcherHolder;
    protected BillingPurchaseFetcherHolder<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseFetcherHolder;
    protected BillingPurchaserHolder<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaserHolder;
>>>>>>> Introduced a billing available tester holder to mimic other holders.

    public BaseBillingLogicHolder()
    {
        super();
<<<<<<< HEAD
        billingAvailableListeners = new HashMap<>();
        testBillingAvailable();
    }

=======
        billingRequests = new HashMap<>();

        billingAvailableTesterHolder= createBillingAvailableTesterHolder();
        productIdentifierFetcherHolder = createProductIdentifierFetcherHolder();
        inventoryFetcherHolder = createInventoryFetcherHolder();
        purchaseFetcherHolder = createPurchaseFetcherHolder();
        purchaserHolder = createPurchaserHolder();
    }

    abstract protected BillingAvailableTesterHolder<BillingExceptionType> createBillingAvailableTesterHolder();
    abstract protected ProductIdentifierFetcherHolder<ProductIdentifierType, BillingExceptionType> createProductIdentifierFetcherHolder();
    abstract protected BillingInventoryFetcherHolder<ProductIdentifierType, ProductDetailType, BillingExceptionType> createInventoryFetcherHolder();
    abstract protected BillingPurchaseFetcherHolder<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> createPurchaseFetcherHolder();
    abstract protected BillingPurchaserHolder<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType, BillingExceptionType> createPurchaserHolder();

>>>>>>> Introduced a billing available tester holder to mimic other holders.
    @Override public void onDestroy()
    {
        if (billingAvailableListeners != null)
        {
            billingAvailableListeners.clear();
        }
<<<<<<< HEAD
=======
        billingRequests.clear();

        billingAvailableTesterHolder.onDestroy();
        productIdentifierFetcherHolder.onDestroy();
        inventoryFetcherHolder.onDestroy();
        purchaseFetcherHolder.onDestroy();
        purchaserHolder.onDestroy();
>>>>>>> Introduced a billing available tester holder to mimic other holders.
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
<<<<<<< HEAD
        return !billingAvailableListeners.containsKey(randomNumber);
=======
        return !billingRequests.containsKey(requestCode);
>>>>>>> Introduced a billing available tester holder to mimic other holders.
    }

    @Override public void forgetRequestCode(int requestCode)
    {
<<<<<<< HEAD
        billingAvailableListeners.remove(requestCode);
=======
        billingRequests.remove(requestCode);

        unregisterProductIdentifierFetchedListener(requestCode);
        unregisterInventoryFetchedListener(requestCode);
        unregisterPurchaseFetchedListener(requestCode);
        unregisterPurchaseFinishedListener(requestCode);
>>>>>>> Introduced a billing available tester holder to mimic other holders.
    }
    //</editor-fold>

    @Override public void registerListeners(int requestCode, BillingRequestType billingRequest)
    {
<<<<<<< HEAD
        registerBillingAvailableListener(requestCode, billingRequest.getBillingAvailableListener());
    }

=======
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
            launchBillingAvailableTestSequence(requestCode);
            launched = true;
        }
        return launched;
    }

    //<editor-fold desc="Launch Sequence Methods">
    public void launchBillingAvailableTestSequence(int requestCode)
    {
        billingAvailableTesterHolder.launchBillingAvailableTestSequence(requestCode);
    }

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
    protected void handleBillingAvailable(int requestCode)
    {
        // TODO child class to continue with other sequence?
    }

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

>>>>>>> Introduced a billing available tester holder to mimic other holders.
    //<editor-fold desc="Billing Available">
    @Override public BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> getBillingAvailableListener(int requestCode)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest == null)
        {
            return null;
        }
        return billingRequest.billingAvailableListener;
    }

    @Override public void registerBillingAvailableListener(int requestCode, BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> billingAvailableListener)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.billingAvailableListener = billingAvailableListener;
            // TODO register in the holder
        }
    }

<<<<<<< HEAD
    protected void notifyBillingAvailable()
    {
        billingAvailable = true;
        OnBillingAvailableListener<BillingExceptionType> availableListener;
        // Protect from unsync when unregistering the listeners
        for (Integer requestCode : new ArrayList<>(billingAvailableListeners.keySet()))
        {
            availableListener = billingAvailableListeners.get(requestCode);
            if (availableListener != null)
            {
                availableListener.onBillingAvailable();
            }
            billingAvailableListeners.remove(requestCode);
=======
    @Override public void unregisterBillingAvailableListener(int requestCode)
    {
        // TODO unregister holder
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.billingAvailableListener = null;
        }
    }

    protected void notifyBillingAvailable(int requestCode)
    {
        BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> availableListener = getBillingAvailableListener(requestCode);
        if (availableListener != null)
        {
            availableListener.onBillingAvailable(requestCode);
>>>>>>> Introduced a billing available tester holder to mimic other holders.
        }
    }

<<<<<<< HEAD
    protected void notifyBillingNotAvailable(BillingExceptionType exception)
    {
        billingAvailable = false;
        OnBillingAvailableListener<BillingExceptionType> availableListener;
        // Protect from unsync when unregistering the listeners
        for (Integer requestCode : new ArrayList<>(billingAvailableListeners.keySet()))
        {
            availableListener = billingAvailableListeners.get(requestCode);
            if (availableListener != null)
=======
    protected void notifyBillingNotAvailable(int requestCode, BillingExceptionType exception)
    {
        BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> availableListener = getBillingAvailableListener(requestCode);
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
>>>>>>> Introduced a billing available tester holder to mimic other holders.
            {
                availableListener.onBillingNotAvailable(exception);
            }
            billingAvailableListeners.remove(requestCode);
        }
    }
    //</editor-fold>
}
