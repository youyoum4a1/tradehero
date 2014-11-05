package com.tradehero.common.billing.request;

import com.tradehero.common.billing.BaseProductIdentifierList;
import com.tradehero.common.billing.BillingAvailableTester;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.BillingPurchaseRestorer;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductIdentifierFetcher;
import com.tradehero.common.billing.ProductIdentifierListKey;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.PurchaseOrder;
import com.tradehero.common.billing.exception.BillingException;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

abstract public class BaseUIBillingRequest<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingExceptionType extends BillingException>
    implements UIBillingRequest<
        ProductIdentifierListKeyType,
        ProductIdentifierType,
        ProductIdentifierListType,
        ProductDetailType,
        PurchaseOrderType,
        OrderIdType,
        ProductPurchaseType,
        BillingExceptionType>
{
    //<editor-fold desc="Generics">
    /**
     * Indicates whether we want a progress dialog to show while stuff is being prepared.
     */
    private final boolean startWithProgressDialog;

    @Override public boolean getStartWithProgressDialog()
    {
        return startWithProgressDialog;
    }
    //</editor-fold>

    //<editor-fold desc="Testing Availability">
    private boolean testBillingAvailable;

    @Override public boolean getTestBillingAvailable()
    {
        return testBillingAvailable;
    }

    @Override public void setTestBillingAvailable(boolean testBillingAvailable)
    {
        this.testBillingAvailable = testBillingAvailable;
    }

    /**
     * Indicates whether we want the Interactor to pop a dialog when billing is not available
     */
    public final boolean popIfBillingNotAvailable;

    @Override public boolean getPopIfBillingNotAvailable()
    {
        return popIfBillingNotAvailable;
    }

    @Nullable protected BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> billingAvailableListener;

    @Nullable @Override public BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> getBillingAvailableListener()
    {
        return billingAvailableListener;
    }

    @Override public void setBillingAvailableListener(@Nullable BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> billingAvailableListener)
    {
        this.billingAvailableListener = billingAvailableListener;
    }
    //</editor-fold>

    //<editor-fold desc="Fetching Product Identifiers">
    private boolean fetchProductIdentifiers;

    @Override public boolean getFetchProductIdentifiers()
    {
        return fetchProductIdentifiers;
    }

    @Override public void setFetchProductIdentifiers(boolean fetchProductIdentifiers)
    {
        this.fetchProductIdentifiers = fetchProductIdentifiers;
    }

    /**
     * Indicates whether we want the Interactor to pop a dialog when the product identifier fetch has failed
     */
    public final boolean popIfProductIdentifierFetchFailed;

    @Override public boolean getPopIfProductIdentifierFetchFailed()
    {
        return popIfProductIdentifierFetchFailed;
    }

    @Nullable protected ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType,
            BillingExceptionType> productIdentifierFetchedListener;

    @Nullable @Override public ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType,
            BillingExceptionType> getProductIdentifierFetchedListener()
    {
        return productIdentifierFetchedListener;
    }

    @Override public void setProductIdentifierFetchedListener(
            @Nullable ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
                    ProductIdentifierListKeyType,
                    ProductIdentifierType,
                    ProductIdentifierListType,
                    BillingExceptionType> productIdentifierFetchedListener)
    {
        this.productIdentifierFetchedListener = productIdentifierFetchedListener;
    }
    //</editor-fold>

    //<editor-fold desc="Fetching Inventory">
    private boolean fetchInventory;

    @Override public boolean getFetchInventory()
    {
        return fetchInventory;
    }

    @Override public void setFetchInventory(boolean fetchInventory)
    {
        this.fetchInventory = fetchInventory;
    }

    /**
     * Indicates whether we want the Interactor to pop a dialog when the inventory fetch has failed
     */
    public final boolean popIfInventoryFetchFailed;

    @Override public boolean getPopIfInventoryFetchFailed()
    {
        return popIfInventoryFetchFailed;
    }

    @Nullable protected BillingInventoryFetcher.OnInventoryFetchedListener<
            ProductIdentifierType,
            ProductDetailType,
            BillingExceptionType> inventoryFetchedListener;

    @Nullable @Override public BillingInventoryFetcher.OnInventoryFetchedListener<
            ProductIdentifierType,
            ProductDetailType,
            BillingExceptionType> getInventoryFetchedListener()
    {
        return inventoryFetchedListener;
    }

    @Override public void setInventoryFetchedListener(
            @Nullable BillingInventoryFetcher.OnInventoryFetchedListener<
                    ProductIdentifierType,
                    ProductDetailType,
                    BillingExceptionType> inventoryFetchedListener)
    {
        this.inventoryFetchedListener = inventoryFetchedListener;
    }
    //</editor-fold>

    //<editor-fold desc="Fetching Purchases">
    private boolean fetchPurchases;

    @Override public boolean getFetchPurchases()
    {
        return fetchPurchases;
    }

    @Override public void setFetchPurchases(boolean fetchPurchases)
    {
        this.fetchPurchases = fetchPurchases;
    }

    /**
     * Indicates whether we want the Interactor to pop a dialog when the fetch of purchases has failed
     */
    public final boolean popIfPurchaseFetchFailed;

    @Override public boolean getPopIfPurchaseFetchFailed()
    {
        return popIfPurchaseFetchFailed;
    }

    @Nullable protected BillingPurchaseFetcher.OnPurchaseFetchedListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> purchaseFetchedListener;

    @Nullable @Override public BillingPurchaseFetcher.OnPurchaseFetchedListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> getPurchaseFetchedListener()
    {
        return purchaseFetchedListener;
    }

    @Override public void setPurchaseFetchedListener(
            @Nullable BillingPurchaseFetcher.OnPurchaseFetchedListener<
                    ProductIdentifierType,
                    OrderIdType,
                    ProductPurchaseType,
                    BillingExceptionType> purchaseFetchedListener)
    {
        this.purchaseFetchedListener = purchaseFetchedListener;
    }
    //</editor-fold>

    //<editor-fold desc="Restoring Purchases">
    private boolean restorePurchase;

    @Override public boolean getRestorePurchase()
    {
        return restorePurchase;
    }

    @Override public void setRestorePurchase(boolean restorePurchase)
    {
        this.restorePurchase = restorePurchase;
    }

    /**
     * Indicates whether we want the Interactor to pop an outcome dialog upon restore.
     */
    private final boolean popRestorePurchaseOutcome;

    @Override public boolean getPopRestorePurchaseOutcome()
    {
        return popRestorePurchaseOutcome;
    }

    /**
     * Indicates whether we want the Interactor to pop an outcome dialog upon restore, even if nothing to report.
     */
    private final boolean popRestorePurchaseOutcomeVerbose;

    @Override public boolean getPopRestorePurchaseOutcomeVerbose()
    {
        return popRestorePurchaseOutcomeVerbose;
    }

    @Nullable protected BillingPurchaseRestorer.OnPurchaseRestorerListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> purchaseRestorerListener;

    @Nullable @Override public BillingPurchaseRestorer.OnPurchaseRestorerListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> getPurchaseRestorerListener()
    {
        return purchaseRestorerListener;
    }

    @Override public void setPurchaseRestorerListener(
            @Nullable BillingPurchaseRestorer.OnPurchaseRestorerListener<
                    ProductIdentifierType,
                    OrderIdType,
                    ProductPurchaseType,
                    BillingExceptionType> purchaseRestorerListener)
    {
        this.purchaseRestorerListener = purchaseRestorerListener;
    }
    //</editor-fold>

    //<editor-fold desc="Purchasing">
    private boolean doPurchase;

    @Override public boolean getDoPurchase()
    {
        return doPurchase;
    }

    @Override public void setDoPurchase(boolean doPurchase)
    {
        this.doPurchase = doPurchase;
    }

    /**
     * Indicates whether we want the Interactor to pop a dialog when the purchase has failed
     */
    private final boolean popIfPurchaseFailed;

    @Override public boolean getPopIfPurchaseFailed()
    {
        return popIfPurchaseFailed;
    }

    @Nullable protected BillingPurchaser.OnPurchaseFinishedListener<
            ProductIdentifierType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> purchaseFinishedListener;

    @Override @Nullable public BillingPurchaser.OnPurchaseFinishedListener<
            ProductIdentifierType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> getPurchaseFinishedListener()
    {
        return purchaseFinishedListener;
    }

    @Override public void setPurchaseFinishedListener(
            @Nullable BillingPurchaser.OnPurchaseFinishedListener<
                    ProductIdentifierType,
                    PurchaseOrderType,
                    OrderIdType,
                    ProductPurchaseType,
                    BillingExceptionType> purchaseFinishedListener)
    {
        this.purchaseFinishedListener = purchaseFinishedListener;
    }
    //</editor-fold>

    //<editor-fold desc="Manage Subscriptions">
    private boolean manageSubscriptions;

    @Override public boolean getManageSubscriptions()
    {
        return manageSubscriptions;
    }

    @Override public void setManageSubscriptions(boolean manageSubscriptions)
    {
        this.manageSubscriptions = manageSubscriptions;
    }
    //</editor-fold>

    //<editor-fold desc="Constructors">
    protected BaseUIBillingRequest(@NonNull Builder<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType,
            ProductDetailType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType,
            ?> builder)
    {
        this.startWithProgressDialog = builder.startWithProgressDialog;

        this.testBillingAvailable = builder.testBillingAvailable;
        this.popIfBillingNotAvailable = builder.popIfBillingNotAvailable;
        this.billingAvailableListener = builder.billingAvailableListener;

        this.fetchProductIdentifiers = builder.fetchProductIdentifiers;
        this.popIfProductIdentifierFetchFailed = builder.popIfProductIdentifierFetchFailed;
        this.productIdentifierFetchedListener = builder.productIdentifierFetchedListener;

        this.fetchInventory = builder.fetchInventory;
        this.popIfInventoryFetchFailed = builder.popIfInventoryFetchFailed;
        this.inventoryFetchedListener = builder.inventoryFetchedListener;

        this.fetchPurchases = builder.fetchPurchases;
        this.popIfPurchaseFetchFailed = builder.popIfPurchaseFetchFailed;
        this.purchaseFetchedListener = builder.purchaseFetchedListener;

        this.restorePurchase = builder.restorePurchase;
        this.popRestorePurchaseOutcome = builder.popRestorePurchaseOutcome;
        this.popRestorePurchaseOutcomeVerbose = builder.popRestorePurchaseOutcomeVerbose;
        this.purchaseRestorerListener = builder.purchaseRestorerListener;

        this.doPurchase = builder.doPurchase;
        this.popIfPurchaseFailed = builder.popIfPurchaseFailed;
        this.purchaseFinishedListener = builder.purchaseFinishedListener;

        this.manageSubscriptions = builder.manageSubscriptions;
    }
    //</editor-fold>

    @Override public void onDestroy()
    {
        billingAvailableListener = null;
        productIdentifierFetchedListener = null;
        inventoryFetchedListener = null;
        purchaseFetchedListener = null;
        purchaseRestorerListener = null;
        purchaseFinishedListener = null;
    }

    @Override abstract public BillingRequest.Builder<
        ProductIdentifierListKeyType,
        ProductIdentifierType,
        ProductIdentifierListType,
        ProductDetailType,
        PurchaseOrderType,
        OrderIdType,
        ProductPurchaseType,
        BillingExceptionType,
        ?> createEmptyBillingRequestBuilder();

    public static abstract class Builder<
            ProductIdentifierListKeyType extends ProductIdentifierListKey,
            ProductIdentifierType extends ProductIdentifier,
            ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
            ProductDetailType extends ProductDetail<ProductIdentifierType>,
            PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
            OrderIdType extends OrderId,
            ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
            BillingExceptionType extends BillingException,
            BuilderType extends Builder<
                    ProductIdentifierListKeyType,
                    ProductIdentifierType,
                    ProductIdentifierListType,
                    ProductDetailType,
                    PurchaseOrderType,
                    OrderIdType,
                    ProductPurchaseType,
                    BillingExceptionType,
                    BuilderType>>
    {
        //<editor-fold desc="Generics">
        private boolean startWithProgressDialog;

        public BuilderType startWithProgressDialog(boolean startWithProgressDialog)
        {
            this.startWithProgressDialog = startWithProgressDialog;
            return self();
        }
        //</editor-fold>

        //<editor-fold desc="Testing Availability">
        private boolean testBillingAvailable;
        private boolean popIfBillingNotAvailable;
        private BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> billingAvailableListener;

        public BuilderType testBillingAvailable(boolean testBillingAvailable)
        {
            this.testBillingAvailable = testBillingAvailable;
            return self();
        }

        public BuilderType popIfBillingNotAvailable(boolean popIfBillingNotAvailable)
        {
            this.popIfBillingNotAvailable = popIfBillingNotAvailable;
            return self();
        }

        public BuilderType billingAvailableListener(BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> billingAvailableListener)
        {
            this.billingAvailableListener = billingAvailableListener;
            return self();
        }
        //</editor-fold>

        //<editor-fold desc="Fetching Product Ientifiers">
        private boolean fetchProductIdentifiers;
        private boolean popIfProductIdentifierFetchFailed;
        @Nullable private ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                BillingExceptionType> productIdentifierFetchedListener;

        public BuilderType fetchProductIdentifiers(boolean fetchProductIdentifiers)
        {
            this.fetchProductIdentifiers = fetchProductIdentifiers;
            return self();
        }

        public BuilderType popIfProductIdentifierFetchFailed(boolean popIfProductIdentifierFetchFailed)
        {
            this.popIfProductIdentifierFetchFailed = popIfProductIdentifierFetchFailed;
            return self();
        }

        public BuilderType productIdentifierFetchedListener(
                @Nullable ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
                        ProductIdentifierListKeyType,
                        ProductIdentifierType,
                        ProductIdentifierListType,
                        BillingExceptionType> productIdentifierFetchedListener)
        {
            this.productIdentifierFetchedListener = productIdentifierFetchedListener;
            return self();
        }
        //</editor-fold>

        //<editor-fold desc="Fetching Inventory">
        private boolean fetchInventory;
        private boolean popIfInventoryFetchFailed;
        @Nullable private BillingInventoryFetcher.OnInventoryFetchedListener<
                ProductIdentifierType,
                ProductDetailType,
                BillingExceptionType> inventoryFetchedListener;

        public BuilderType fetchInventory(boolean fetchInventory)
        {
            this.fetchInventory = fetchInventory;
            return self();
        }

        public BuilderType popIfInventoryFetchFailed(boolean popIfInventoryFetchFailed)
        {
            this.popIfInventoryFetchFailed = popIfInventoryFetchFailed;
            return self();
        }

        public BuilderType inventoryFetchedListener(
                @Nullable BillingInventoryFetcher.OnInventoryFetchedListener<
                        ProductIdentifierType,
                        ProductDetailType,
                        BillingExceptionType> inventoryFetchedListener)
        {
            this.inventoryFetchedListener = inventoryFetchedListener;
            return self();
        }
        //</editor-fold>

        //<editor-fold desc="Fetching Purchases">
        private boolean fetchPurchases;
        private boolean popIfPurchaseFetchFailed;
        @Nullable private BillingPurchaseFetcher.OnPurchaseFetchedListener<
                ProductIdentifierType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType> purchaseFetchedListener;

        public BuilderType fetchPurchases(boolean fetchPurchases)
        {
            this.fetchPurchases = fetchPurchases;
            return self();
        }

        public BuilderType popIfPurchaseFetchFailed(boolean popIfPurchaseFetchFailed)
        {
            this.popIfPurchaseFetchFailed = popIfPurchaseFetchFailed;
            return self();
        }

        public BuilderType purchaseFetchedListener(
                @Nullable BillingPurchaseFetcher.OnPurchaseFetchedListener<
                        ProductIdentifierType,
                        OrderIdType,
                        ProductPurchaseType,
                        BillingExceptionType> purchaseFetchedListener)
        {
            this.purchaseFetchedListener = purchaseFetchedListener;
            return self();
        }
        //</editor-fold>

        //<editor-fold desc="Restoring Purchases">
        private boolean restorePurchase;
        private boolean popRestorePurchaseOutcome;
        private boolean popRestorePurchaseOutcomeVerbose;
        @Nullable private BillingPurchaseRestorer.OnPurchaseRestorerListener<
                ProductIdentifierType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType> purchaseRestorerListener;

        public BuilderType restorePurchase(boolean restorePurchase)
        {
            this.restorePurchase = restorePurchase;
            return self();
        }

        public BuilderType popRestorePurchaseOutcome(boolean popRestorePurchaseOutcome)
        {
            this.popRestorePurchaseOutcome = popRestorePurchaseOutcome;
            return self();
        }

        public BuilderType popRestorePurchaseOutcomeVerbose(boolean popRestorePurchaseOutcomeVerbose)
        {
            this.popRestorePurchaseOutcomeVerbose = popRestorePurchaseOutcomeVerbose;
            return self();
        }

        public BuilderType purchaseRestorerListener(
                @Nullable BillingPurchaseRestorer.OnPurchaseRestorerListener<
                        ProductIdentifierType,
                        OrderIdType,
                        ProductPurchaseType,
                        BillingExceptionType> purchaseRestorerListener)
        {
            this.purchaseRestorerListener = purchaseRestorerListener;
            return self();
        }
        //</editor-fold>

        //<editor-fold desc="Purchasing">
        private boolean doPurchase;
        private boolean popIfPurchaseFailed;
        @Nullable private BillingPurchaser.OnPurchaseFinishedListener<
                ProductIdentifierType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType> purchaseFinishedListener;

        public BuilderType doPurchase(boolean doPurchase)
        {
            this.doPurchase = doPurchase;
            return self();
        }

        public BuilderType popIfPurchaseFailed(boolean popIfPurchaseFailed)
        {
            this.popIfPurchaseFailed = popIfPurchaseFailed;
            return self();
        }

        public BuilderType purchaseFinishedListener(
                @Nullable BillingPurchaser.OnPurchaseFinishedListener<
                        ProductIdentifierType,
                        PurchaseOrderType,
                        OrderIdType,
                        ProductPurchaseType,
                        BillingExceptionType> purchaseFinishedListener)
        {
            this.purchaseFinishedListener = purchaseFinishedListener;
            return self();
        }
        //</editor-fold>

        //<editor-fold desc="Manage Subscriptions">
        private boolean manageSubscriptions;

        public BuilderType manageSubscriptions(boolean manageSubscriptions)
        {
            this.manageSubscriptions = manageSubscriptions;
            return self();
        }
        //</editor-fold>

        abstract protected BuilderType self();
        abstract public BaseUIBillingRequest<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                ProductDetailType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType> build();
    }

    @Override public String toString()
    {
        return "BaseUIBillingRequest:{" +
                "startWithProgressDialog=" + startWithProgressDialog +
                ", testBillingAvailable=" + testBillingAvailable +
                ", popIfBillingNotAvailable=" + popIfBillingNotAvailable +
                ", billingAvailableListener=" + billingAvailableListener +
                ", fetchProductIdentifiers=" + fetchProductIdentifiers +
                ", popIfProductIdentifierFetchFailed=" + popIfProductIdentifierFetchFailed +
                ", productIdentifierFetchedListener=" + productIdentifierFetchedListener +
                ", fetchInventory=" + fetchInventory +
                ", popIfInventoryFetchFailed=" + popIfInventoryFetchFailed +
                ", inventoryFetchedListener=" + inventoryFetchedListener +
                ", fetchPurchases=" + fetchPurchases +
                ", popIfPurchaseFetchFailed=" + popIfPurchaseFetchFailed +
                ", purchaseFetchedListener=" + purchaseFetchedListener +
                ", restorePurchase=" + restorePurchase +
                ", popRestorePurchaseOutcome=" + popRestorePurchaseOutcome +
                ", popRestorePurchaseOutcomeVerbose=" + popRestorePurchaseOutcomeVerbose +
                ", purchaseRestorerListener=" + purchaseRestorerListener +
                ", doPurchase=" + doPurchase +
                ", popIfPurchaseFailed=" + popIfPurchaseFailed +
                ", purchaseFinishedListener=" + purchaseFinishedListener +
                '}';
    }
}
