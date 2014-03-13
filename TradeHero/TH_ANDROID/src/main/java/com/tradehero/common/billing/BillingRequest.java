package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;

/**
 * Created by xavier on 3/13/14.
 */
public class BillingRequest<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingExceptionType extends BillingException>
{
    public static final String TAG = BillingRequest.class.getSimpleName();

    private BillingInventoryFetcher.OnInventoryFetchedListener<
            ProductIdentifierType,
            ProductDetailType,
            BillingExceptionType> inventoryFetchedListener;
    private BillingPurchaseFetcher.OnPurchaseFetchedListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> purchaseFetchedListener;
    private BillingPurchaser.OnPurchaseFinishedListener<
            ProductIdentifierType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> purchaseFinishedListener;

    protected BillingRequest(
            BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> inventoryFetchedListener,
            BillingPurchaseFetcher.OnPurchaseFetchedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseFetchedListener,
            BillingPurchaser.OnPurchaseFinishedListener<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseFinishedListener)
    {
        this.inventoryFetchedListener = inventoryFetchedListener;
        this.purchaseFetchedListener = purchaseFetchedListener;
        this.purchaseFinishedListener = purchaseFinishedListener;
    }

    //<editor-fold desc="Accessors">
    public BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> getInventoryFetchedListener()
    {
        return inventoryFetchedListener;
    }

    public void setInventoryFetchedListener(
            BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> inventoryFetchedListener)
    {
        this.inventoryFetchedListener = inventoryFetchedListener;
    }

    public BillingPurchaseFetcher.OnPurchaseFetchedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> getPurchaseFetchedListener()
    {
        return purchaseFetchedListener;
    }

    public void setPurchaseFetchedListener(
            BillingPurchaseFetcher.OnPurchaseFetchedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseFetchedListener)
    {
        this.purchaseFetchedListener = purchaseFetchedListener;
    }

    public BillingPurchaser.OnPurchaseFinishedListener<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType, BillingExceptionType> getPurchaseFinishedListener()
    {
        return purchaseFinishedListener;
    }

    public void setPurchaseFinishedListener(
            BillingPurchaser.OnPurchaseFinishedListener<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseFinishedListener)
    {
        this.purchaseFinishedListener = purchaseFinishedListener;
    }
    //</editor-fold>

    public static class Builder<
            ProductIdentifierType extends ProductIdentifier,
            ProductDetailType extends ProductDetail<ProductIdentifierType>,
            PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
            OrderIdType extends OrderId,
            ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
            BillingExceptionType extends BillingException>
    {
        private BillingInventoryFetcher.OnInventoryFetchedListener<
                ProductIdentifierType,
                ProductDetailType,
                BillingExceptionType> inventoryFetchedListener;
        private BillingPurchaseFetcher.OnPurchaseFetchedListener<
                ProductIdentifierType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType> purchaseFetchedListener;
        private BillingPurchaser.OnPurchaseFinishedListener<
                ProductIdentifierType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType> purchaseFinishedListener;

        public Builder()
        {
        }

        public BillingRequest<ProductIdentifierType, ProductDetailType, PurchaseOrderType, OrderIdType, ProductPurchaseType, BillingExceptionType> build()
        {
            return new BillingRequest<>(inventoryFetchedListener, purchaseFetchedListener, purchaseFinishedListener);
        }

        //<editor-fold desc="Accessors">
        public BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> getInventoryFetchedListener()
        {
            return inventoryFetchedListener;
        }

        public void setInventoryFetchedListener(
                BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> inventoryFetchedListener)
        {
            this.inventoryFetchedListener = inventoryFetchedListener;
        }

        public BillingPurchaseFetcher.OnPurchaseFetchedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> getPurchaseFetchedListener()
        {
            return purchaseFetchedListener;
        }

        public void setPurchaseFetchedListener(
                BillingPurchaseFetcher.OnPurchaseFetchedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseFetchedListener)
        {
            this.purchaseFetchedListener = purchaseFetchedListener;
        }

        public BillingPurchaser.OnPurchaseFinishedListener<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType, BillingExceptionType> getPurchaseFinishedListener()
        {
            return purchaseFinishedListener;
        }

        public void setPurchaseFinishedListener(
                BillingPurchaser.OnPurchaseFinishedListener<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseFinishedListener)
        {
            this.purchaseFinishedListener = purchaseFinishedListener;
        }
        //</editor-fold>
    }
}
