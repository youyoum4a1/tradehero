package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.request.BillingRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import timber.log.Timber;


abstract public class BaseBillingLogicHolder<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        ProductTunerType extends ProductDetailTuner<ProductIdentifierType, ProductDetailType>,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingRequestType extends BillingRequest<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                ProductDetailType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType>,
        BillingExceptionType extends BillingException>
    implements BillingLogicHolder<
        ProductIdentifierListKeyType,
        ProductIdentifierType,
        ProductIdentifierListType,
        ProductDetailType,
        PurchaseOrderType,
        OrderIdType,
        ProductPurchaseType,
        BillingRequestType,
        BillingExceptionType>
{
    public static final int MAX_RANDOM_RETRIES = 50;

    protected Map<Integer, BillingRequestType> billingRequests;

    protected BillingAvailableTesterHolder<BillingExceptionType> billingAvailableTesterHolder;
    protected ProductIdentifierFetcherHolder<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType, BillingExceptionType> productIdentifierFetcherHolder;
    protected BillingInventoryFetcherHolder<ProductIdentifierType, ProductDetailType, BillingExceptionType> inventoryFetcherHolder;
    protected BillingPurchaseFetcherHolder<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseFetcherHolder;
    protected BillingPurchaserHolder<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaserHolder;

    public BaseBillingLogicHolder()
    {
        super();
        billingRequests = new HashMap<>();

        billingAvailableTesterHolder= createBillingAvailableTesterHolder();
        productIdentifierFetcherHolder = createProductIdentifierFetcherHolder();
        inventoryFetcherHolder = createInventoryFetcherHolder();
        purchaseFetcherHolder = createPurchaseFetcherHolder();
        purchaserHolder = createPurchaserHolder();
    }

    //<editor-fold desc="Life Cycle">
    abstract protected BillingAvailableTesterHolder<BillingExceptionType> createBillingAvailableTesterHolder();
    abstract protected ProductIdentifierFetcherHolder<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType, BillingExceptionType> createProductIdentifierFetcherHolder();
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

        billingAvailableTesterHolder.onDestroy();
        productIdentifierFetcherHolder.onDestroy();
        inventoryFetcherHolder.onDestroy();
        purchaseFetcherHolder.onDestroy();
        purchaserHolder.onDestroy();
    }
    //</editor-fold>

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
        return !billingRequests.containsKey(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        billingRequests.remove(requestCode);

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
        registerPurchaseRestorerListener(requestCode, billingRequest.purchaseRestorerListener);
        registerPurchaseFinishedListener(requestCode, billingRequest.purchaseFinishedListener);
    }

    //<editor-fold desc="Run Logic">
    /**
     *
     * @param requestCode
     * @param billingRequest
     * @return true if sequence launched, false otherwise
     */
    @Override public boolean run(int requestCode, BillingRequestType billingRequest)
    {
        billingRequests.put(requestCode, billingRequest);
        registerListeners(requestCode, billingRequest);
        return runInternal(requestCode);
    }

    protected boolean runInternal(int requestCode)
    {
        boolean launched = false;
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            if (billingRequest.testBillingAvailable)
            {
                launchBillingAvailableTestSequence(requestCode);
                launched = true;
            }
            else if (billingRequest.fetchProductIdentifiers)
            {
                launchProductIdentifierFetchSequence(requestCode);
                launched = true;
            }
            else if (billingRequest.fetchInventory)
            {
                launchInventoryFetchSequence(requestCode, billingRequest.productIdentifiersForInventory);
                launched = true;
            }
            else if (billingRequest.fetchPurchase)
            {
                launchFetchPurchaseSequence(requestCode);
                launched = true;
            }
            else if (billingRequest.doPurchase && billingRequest.purchaseOrder != null)
            {
                launchPurchaseSequence(requestCode, billingRequest.purchaseOrder);
                launched = true;
            }
            // Restore is better placed in the bottom-child class as it involves repeating similar actions.
            // When this is the last element to test, we are sure that all individual actions have been tested
            // or run
        }
        return launched;
    }

    abstract protected boolean prepareToRestoreOnePurchase(int requestCode, BillingRequestType billingRequest);
    //</editor-fold>

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
        Timber.d("Billing available %d", requestCode);
        notifyBillingAvailable(requestCode);
        prepareRequestForNextRunAfterBillingAvailable(requestCode);
        runInternal(requestCode);
    }

    protected void prepareRequestForNextRunAfterBillingAvailable(int requestCode)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.testBillingAvailable = false;
        }
    }

    protected void handleBillingNotAvailable(int requestCode, BillingExceptionType exception)
    {
        Timber.d("Billing not available %d", requestCode);
        notifyBillingNotAvailable(requestCode, exception);
        prepareRequestForNextRunAfterBillingNotAvailable(requestCode, exception);
        runInternal(requestCode);
    }

    protected void prepareRequestForNextRunAfterBillingNotAvailable(int requestCode, BillingExceptionType exception)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.testBillingAvailable = false;
        }
    }

    protected void handleProductIdentifierFetchedSuccess(int requestCode, Map<ProductIdentifierListKeyType, ProductIdentifierListType> availableProductIdentifiers)
    {
        getProductIdentifierCache().put(availableProductIdentifiers);
        notifyProductIdentifierFetchedSuccess(requestCode, availableProductIdentifiers);
        prepareRequestForNextRunAfterProductIdentifierFetchedSuccess(requestCode, availableProductIdentifiers);
        runInternal(requestCode);
    }

    protected void prepareRequestForNextRunAfterProductIdentifierFetchedSuccess(int requestCode, Map<ProductIdentifierListKeyType, ProductIdentifierListType> availableProductIdentifiers)
    {
        List<ProductIdentifierType> all = new ArrayList<>();
        for (Map.Entry<ProductIdentifierListKeyType, ProductIdentifierListType> entry : availableProductIdentifiers.entrySet())
        {
            all.addAll(entry.getValue());
        }
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.fetchProductIdentifiers = false;
            billingRequest.productIdentifiersForInventory = all;
        }
    }

    protected void handleProductIdentifierFetchedFailed(int requestCode, BillingExceptionType exception)
    {
        notifyProductIdentifierFetchedFailed(requestCode, exception);
        prepareRequestForNextRunAfterProductIdentifierFetchedFailed(requestCode, exception);
        runInternal(requestCode);
    }

    protected void prepareRequestForNextRunAfterProductIdentifierFetchedFailed(int requestCode, BillingExceptionType exception)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.fetchProductIdentifiers = false;
        }
    }

    protected void handleInventoryFetchedSuccess(int requestCode, List<ProductIdentifierType> productIdentifiers, Map<ProductIdentifierType, ProductDetailType> inventory)
    {
        getProductDetailCache().put(inventory);
        notifyInventoryFetchedSuccess(requestCode, productIdentifiers, inventory);
        prepareRequestForNextRunAfterInventoryFetchedSuccess(requestCode, productIdentifiers, inventory);
        runInternal(requestCode);
    }

    protected void prepareRequestForNextRunAfterInventoryFetchedSuccess(int requestCode, List<ProductIdentifierType> productIdentifiers, Map<ProductIdentifierType, ProductDetailType> inventory)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.fetchInventory = false;
        }
    }

    protected void handleInventoryFetchFailed(int requestCode, List<ProductIdentifierType> productIdentifiers, BillingExceptionType exception)
    {
        notifyInventoryFetchFailed(requestCode, productIdentifiers, exception);
        prepareRequestForNextRunAfterInventoryFetchFailed(requestCode, productIdentifiers, exception);
        runInternal(requestCode);
    }

    protected void prepareRequestForNextRunAfterInventoryFetchFailed(int requestCode, List<ProductIdentifierType> productIdentifiers, BillingExceptionType exception)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.fetchInventory = false;
        }
    }

    protected void handlePurchaseFetchedSuccess(int requestCode, List<ProductPurchaseType> purchases)
    {
        notifyPurchaseFetchedSuccess(requestCode, purchases);
        prepareRequestForNextRunAfterPurchaseFetchedSuccess(requestCode, purchases);
        runInternal(requestCode);
    }

    protected void prepareRequestForNextRunAfterPurchaseFetchedSuccess(int requestCode, List<ProductPurchaseType> purchases)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.fetchPurchase = false;
            if (purchases != null)
            {
                billingRequest.fetchedPurchases = new LinkedList<>();
                for (ProductPurchaseType productPurchase : purchases)
                {
                    billingRequest.fetchedPurchases.add(productPurchase);
                }
            }
        }
    }

    protected void handlePurchaseFetchedFailed(int requestCode, BillingExceptionType exception)
    {
        notifyPurchaseFetchedFailed(requestCode, exception);
        prepareRequestForNextRunAfterPurchaseFetchedFailed(requestCode, exception);
        runInternal(requestCode);
    }

    protected void prepareRequestForNextRunAfterPurchaseFetchedFailed(int requestCode, BillingExceptionType exception)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.fetchPurchase = false;
        }
    }

    protected void handlePurchaseFinished(int requestCode, PurchaseOrderType purchaseOrder, ProductPurchaseType purchase)
    {
        notifyPurchaseFinished(requestCode, purchaseOrder, purchase);
        prepareRequestForNextRunAfterPurchaseFinished(requestCode, purchaseOrder, purchase);
        runInternal(requestCode);
    }

    protected void prepareRequestForNextRunAfterPurchaseFinished(int requestCode, PurchaseOrderType purchaseOrder, ProductPurchaseType purchase)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.doPurchase = false;
        }
    }

    protected void handlePurchaseFailed(int requestCode, PurchaseOrderType purchaseOrder, BillingExceptionType billingException)
    {
        notifyPurchaseFailed(requestCode, purchaseOrder, billingException);
        prepareRequestForNextRunAfterPurchaseFailed(requestCode, purchaseOrder, billingException);
        runInternal(requestCode);
    }

    protected void prepareRequestForNextRunAfterPurchaseFailed(int requestCode, PurchaseOrderType purchaseOrder, BillingExceptionType billingException)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.doPurchase = false;
        }
    }
    //</editor-fold>

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
            billingAvailableTesterHolder.registerBillingAvailableListener(requestCode, createBillingAvailableListener());
        }
    }

    protected BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> createBillingAvailableListener()
    {
        return new BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType>()
        {
            @Override public void onBillingAvailable(int requestCode)
            {
                handleBillingAvailable(requestCode);
            }

            @Override public void onBillingNotAvailable(int requestCode, BillingExceptionType billingException)
            {
                handleBillingNotAvailable(requestCode, billingException);
            }
        };
    }

    @Override public void unregisterBillingAvailableListener(int requestCode)
    {
        billingAvailableTesterHolder.forgetRequestCode(requestCode);
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
        }
        unregisterBillingAvailableListener(requestCode);
    }

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
    abstract protected ProductIdentifierListCache<ProductIdentifierType, ProductIdentifierListKeyType, ProductIdentifierListType> getProductIdentifierCache();

    @Override
    public ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType, BillingExceptionType> getProductIdentifierFetchedListener(int requestCode)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest == null)
        {
            return null;
        }
        return billingRequest.productIdentifierFetchedListener;
    }

    @Override public void registerProductIdentifierFetchedListener(int requestCode, ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType, BillingExceptionType> productIdentifierFetchedListener)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.productIdentifierFetchedListener = productIdentifierFetchedListener;
            productIdentifierFetcherHolder.registerProductIdentifierFetchedListener(requestCode, createProductIdentifierFetchedListener());
        }
    }

    protected ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType, BillingExceptionType> createProductIdentifierFetchedListener()
    {
        return new ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType, BillingExceptionType>()
        {
            @Override public void onFetchedProductIdentifiers(int requestCode, Map<ProductIdentifierListKeyType, ProductIdentifierListType> availableProductIdentifiers)
            {
                handleProductIdentifierFetchedSuccess(requestCode, availableProductIdentifiers);
            }

            @Override public void onFetchProductIdentifiersFailed(int requestCode, BillingExceptionType exception)
            {
                handleProductIdentifierFetchedFailed(requestCode, exception);
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

    protected void notifyProductIdentifierFetchedSuccess(int requestCode, Map<ProductIdentifierListKeyType, ProductIdentifierListType> availableProductIdentifiers)
    {
        ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType, BillingExceptionType> productIdentifierFetchedListener = getProductIdentifierFetchedListener(requestCode);
        if (productIdentifierFetchedListener != null)
        {
            productIdentifierFetchedListener.onFetchedProductIdentifiers(requestCode, availableProductIdentifiers);
        }
        unregisterProductIdentifierFetchedListener(requestCode);
    }

    protected void notifyProductIdentifierFetchedFailed(int requestCode, BillingExceptionType exception)
    {
        ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType, BillingExceptionType> productIdentifierFetchedListener = getProductIdentifierFetchedListener(requestCode);
        if (productIdentifierFetchedListener != null)
        {
            productIdentifierFetchedListener.onFetchProductIdentifiersFailed(requestCode, exception);
        }
        unregisterProductIdentifierFetchedListener(requestCode);
    }
    //</editor-fold>

    //<editor-fold desc="Fetch Inventory">
    abstract protected ProductDetailCache<ProductIdentifierType, ProductDetailType, ProductTunerType> getProductDetailCache();

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
                handleInventoryFetchFailed(requestCode, productIdentifiers, exception);
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
            @Override public void onFetchedPurchases(int requestCode, List<ProductPurchaseType> purchases)
            {
                handlePurchaseFetchedSuccess(requestCode, purchases);
            }

            @Override public void onFetchPurchasesFailed(int requestCode, BillingExceptionType exception)
            {
                handlePurchaseFetchedFailed(requestCode, exception);
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

    protected void notifyPurchaseFetchedSuccess(int requestCode, List<ProductPurchaseType> purchases)
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

    //<editor-fold desc="Restore Purchase">
    @Override public BillingPurchaseRestorer.OnPurchaseRestorerListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> getPurchaseRestorerListener(int requestCode)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest == null)
        {
            return null;
        }
        return billingRequest.purchaseRestorerListener;
    }

    @Override public void registerPurchaseRestorerListener(int requestCode, BillingPurchaseRestorer.OnPurchaseRestorerListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseRestorerListener)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.purchaseRestorerListener = purchaseRestorerListener;
        }
    }

    @Override public void unregisterPurchaseRestorerListener(int requestCode)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.purchaseRestorerListener = null;
        }
    }

    protected void notifyPurchaseRestored(int requestCode, List<ProductPurchaseType> restoredPurchases,
            List<ProductPurchaseType> failedRestorePurchases, List<BillingExceptionType> failExceptions)
    {
        BillingPurchaseRestorer.OnPurchaseRestorerListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseFetchedListener = getPurchaseRestorerListener(
                requestCode);
        if (purchaseFetchedListener != null)
        {
            purchaseFetchedListener.onPurchaseRestored(requestCode, restoredPurchases, failedRestorePurchases, failExceptions);
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
                handlePurchaseFailed(requestCode, purchaseOrder, exception);
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
        else
        {
            Timber.d("No purchase finished listener for failed");
        }
        unregisterPurchaseFinishedListener(requestCode);
    }
    //</editor-fold>
}
