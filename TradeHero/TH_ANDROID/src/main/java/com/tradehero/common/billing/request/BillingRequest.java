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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BillingRequest<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingExceptionType extends BillingException>
{
    /**
     * Indicates whether we want to test if billing is available
     */
    public boolean testBillingAvailable;
    @Nullable public BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> billingAvailableListener;

    /**
     * Indicates whether we want to fetch the product identifiers
     */
    public boolean fetchProductIdentifiers;
    @Nullable public ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType,
            BillingExceptionType> productIdentifierFetchedListener;

    /**
     * Indicates whether we want to fetch the product details
     */
    public boolean fetchInventory;
    public List<ProductIdentifierType> productIdentifiersForInventory;
    @Nullable public BillingInventoryFetcher.OnInventoryFetchedListener<
            ProductIdentifierType,
            ProductDetailType,
            BillingExceptionType> inventoryFetchedListener;

    /**
     * Indicates whether we want to fetch the purchases
     */
    public boolean fetchPurchase;
    @Nullable public BillingPurchaseFetcher.OnPurchaseFetchedListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> purchaseFetchedListener;
    public LinkedList<ProductPurchaseType> fetchedPurchases;

    /**
     * Indicates whether we want to restore purchases
     */
    public final boolean restorePurchase;
    public List<ProductPurchaseType> restoredPurchases = new ArrayList<>();
    public List<ProductPurchaseType> restoreFailedPurchases = new ArrayList<>();
    public List<BillingExceptionType> restoreFailedErrors = new ArrayList<>();
    @Nullable public BillingPurchaseRestorer.OnPurchaseRestorerListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseRestorerListener;

    public boolean doPurchase;
    public PurchaseOrderType purchaseOrder;
    @Nullable public BillingPurchaser.OnPurchaseFinishedListener<
            ProductIdentifierType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> purchaseFinishedListener;

    public boolean manageSubscriptions;

    //<editor-fold desc="Constructors">
    protected BillingRequest(@NonNull Builder<
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
        this.testBillingAvailable = builder.testBillingAvailable;
        this.billingAvailableListener = builder.billingAvailableListener;
        this.fetchProductIdentifiers = builder.fetchProductIdentifiers;
        this.productIdentifierFetchedListener = builder.productIdentifierFetchedListener;
        this.fetchInventory = builder.fetchInventory;
        this.productIdentifiersForInventory = builder.productIdentifiersForInventory;
        this.inventoryFetchedListener = builder.inventoryFetchedListener;
        this.fetchPurchase = builder.fetchPurchase;
        this.purchaseFetchedListener = builder.purchaseFetchedListener;
        this.restorePurchase = builder.restorePurchase;
        this.purchaseRestorerListener = builder.purchaseRestorerListener;
        this.doPurchase = builder.doPurchase;
        this.purchaseOrder = builder.purchaseOrder;
        this.purchaseFinishedListener = builder.purchaseFinishedListener;
        this.manageSubscriptions = builder.manageSubscriptions;
    }
    //</editor-fold>

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
        //<editor-fold desc="Constructors">
        protected Builder()
        {
        }
        //</editor-fold>

        //<editor-fold desc="Is Billing Available">
        private boolean testBillingAvailable;
        @Nullable private BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> billingAvailableListener;

        public BuilderType testBillingAvailable(boolean testBillingAvailable)
        {
            this.testBillingAvailable = testBillingAvailable;
            return self();
        }

        public BuilderType billingAvailableListener(
                @Nullable BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> billingAvailableListener)
        {
            this.billingAvailableListener = billingAvailableListener;
            return self();
        }
        //</editor-fold>

        //<editor-fold desc="Need to Fetch Product Identifiers">
        private boolean fetchProductIdentifiers;
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

        public BuilderType productIdentifierFetchedListener(
                @Nullable ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType, BillingExceptionType> productIdentifierFetchedListener)
        {
            this.productIdentifierFetchedListener = productIdentifierFetchedListener;
            return self();
        }
        //</editor-fold>

        //<editor-fold desc="Need to Fetch Inventory">
        private boolean fetchInventory;
        private List<ProductIdentifierType> productIdentifiersForInventory;
        @Nullable private BillingInventoryFetcher.OnInventoryFetchedListener<
                ProductIdentifierType,
                ProductDetailType,
                BillingExceptionType> inventoryFetchedListener;

        public BuilderType fetchInventory(boolean fetchInventory)
        {
            this.fetchInventory = fetchInventory;
            return self();
        }

        public BuilderType productIdentifiersForInventory(
                List<ProductIdentifierType> productIdentifiersForInventory)
        {
            this.productIdentifiersForInventory = productIdentifiersForInventory;
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

        //<editor-fold desc="Need to Fetch Purchases">
        private boolean fetchPurchase;
        @Nullable private BillingPurchaseFetcher.OnPurchaseFetchedListener<
                ProductIdentifierType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType> purchaseFetchedListener;

        public BuilderType fetchPurchases(boolean fetchPurchase)
        {
            this.fetchPurchase = fetchPurchase;
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

        //<editor-fold desc="Need to Restore Purchases">
        private boolean restorePurchase;
        @Nullable private BillingPurchaseRestorer.OnPurchaseRestorerListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseRestorerListener;

        public BuilderType restorePurchase(boolean restorePurchase)
        {
            this.restorePurchase = restorePurchase;
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

        //<editor-fold desc="Need to Purchase">
        private boolean doPurchase;
        private PurchaseOrderType purchaseOrder;
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

        public BuilderType purchaseOrder(
                PurchaseOrderType purchaseOrder)
        {
            this.purchaseOrder = purchaseOrder;
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

        protected abstract BuilderType self();

        public BillingRequest build()
        {
            return new BillingRequest<>(this);
        }
    }

    public void onDestroy()
    {
        this.billingAvailableListener = null;
        this.productIdentifierFetchedListener = null;
        this.inventoryFetchedListener = null;
        this.purchaseFetchedListener = null;
        this.purchaseFinishedListener = null;
    }

    @Override public String toString()
    {
        return "BillingRequest:{" +
                "testBillingAvailable=" + testBillingAvailable +
                ", billingAvailableListener=" + billingAvailableListener +
                ", fetchProductIdentifiers=" + fetchProductIdentifiers +
                ", productIdentifierFetchedListener=" + productIdentifierFetchedListener +
                ", fetchInventory=" + fetchInventory +
                ", productIdentifiersForInventory=" + productIdentifiersForInventory +
                ", inventoryFetchedListener=" + inventoryFetchedListener +
                ", fetchPurchases=" + fetchPurchase +
                ", purchaseFetchedListener=" + purchaseFetchedListener +
                ", fetchedPurchases=" + fetchedPurchases +
                ", restorePurchase=" + restorePurchase +
                ", restoredPurchases=" + restoredPurchases +
                ", restoreFailedPurchases=" + restoreFailedPurchases +
                ", restoreFailedErrors=" + restoreFailedErrors +
                ", purchaseRestorerListener=" + purchaseRestorerListener +
                ", doPurchase=" + doPurchase +
                ", purchaseOrder=" + purchaseOrder +
                ", purchaseFinishedListener=" + purchaseFinishedListener +
                '}';
    }
}
