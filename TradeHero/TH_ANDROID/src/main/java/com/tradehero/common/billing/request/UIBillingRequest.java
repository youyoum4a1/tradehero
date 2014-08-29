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
import org.jetbrains.annotations.Nullable;

public interface UIBillingRequest<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingExceptionType extends BillingException>
{
    //<editor-fold desc="Generics">
    boolean getStartWithProgressDialog();
    //</editor-fold>

    //<editor-fold desc="Testing Availability">
    boolean getTestBillingAvailable();
    void setTestBillingAvailable(boolean testBillingAvailable);
    boolean getPopIfBillingNotAvailable();
    @Nullable BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> getBillingAvailableListener();
    void setBillingAvailableListener(
            @Nullable BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> billingAvailableListener);
    //</editor-fold>

    //<editor-fold desc="Fetching Product Identifiers">
    boolean getFetchProductIdentifiers();
    void setFetchProductIdentifiers(boolean fetchProductIdentifiers);
    boolean getPopIfProductIdentifierFetchFailed();
    @Nullable ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType,
            BillingExceptionType> getProductIdentifierFetchedListener();
    void setProductIdentifierFetchedListener(
            @Nullable ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
                    ProductIdentifierListKeyType,
                    ProductIdentifierType,
                    ProductIdentifierListType,
                    BillingExceptionType> productIdentifierFetchedListener);
    //</editor-fold>

    //<editor-fold desc="Fetching Inventory">
    boolean getFetchInventory();
    void setFetchInventory(boolean fetchInventory);
    boolean getPopIfInventoryFetchFailed();
    @Nullable BillingInventoryFetcher.OnInventoryFetchedListener<
            ProductIdentifierType,
            ProductDetailType,
            BillingExceptionType> getInventoryFetchedListener();
    void setInventoryFetchedListener(
            @Nullable BillingInventoryFetcher.OnInventoryFetchedListener<
                    ProductIdentifierType,
                    ProductDetailType,
                    BillingExceptionType> inventoryFetchedListener);
    //</editor-fold>

    //<editor-fold desc="Fetching Purchases">
    boolean getFetchPurchases();
    void setFetchPurchases(boolean fetchPurchases);
    boolean getPopIfPurchaseFetchFailed();
    @Nullable BillingPurchaseFetcher.OnPurchaseFetchedListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> getPurchaseFetchedListener();
    void setPurchaseFetchedListener(
            @Nullable BillingPurchaseFetcher.OnPurchaseFetchedListener<
                    ProductIdentifierType,
                    OrderIdType,
                    ProductPurchaseType,
                    BillingExceptionType> purchaseFetchedListener);
    //</editor-fold>

    //<editor-fold desc="Restoring Purchases">
    boolean getRestorePurchase();
    void setRestorePurchase(boolean restorePurchases);
    boolean getPopRestorePurchaseOutcome();
    boolean getPopRestorePurchaseOutcomeVerbose();
    @Nullable public BillingPurchaseRestorer.OnPurchaseRestorerListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> getPurchaseRestorerListener();
    void setPurchaseRestorerListener(
            @Nullable BillingPurchaseRestorer.OnPurchaseRestorerListener<
                    ProductIdentifierType,
                    OrderIdType,
                    ProductPurchaseType,
                    BillingExceptionType> purchaseRestorerListener);
    //</editor-fold>

    //<editor-fold desc="Purchasing">
    boolean getDoPurchase();
    void setDoPurchase(boolean doPurchase);
    boolean getPopIfPurchaseFailed();
    @Nullable BillingPurchaser.OnPurchaseFinishedListener<
            ProductIdentifierType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> getPurchaseFinishedListener();
    void setPurchaseFinishedListener(
            @Nullable BillingPurchaser.OnPurchaseFinishedListener<
                    ProductIdentifierType,
                    PurchaseOrderType,
                    OrderIdType,
                    ProductPurchaseType,
                    BillingExceptionType> purchaseFinishedListener);
    //</editor-fold>

    //<editor-fold desc="Manage Subscriptions">
    boolean getManageSubscriptions();
    void setManageSubscriptions(boolean manageSubscriptions);
    //</editor-fold>

    void onDestroy();

    BillingRequest.Builder<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType,
            ProductDetailType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType,
            ?> createEmptyBillingRequestBuilder();

    public static interface OnErrorListener<BillingExceptionType extends BillingException>
    {
        void onError(int requestCode, BillingExceptionType billingException);
    }
}
