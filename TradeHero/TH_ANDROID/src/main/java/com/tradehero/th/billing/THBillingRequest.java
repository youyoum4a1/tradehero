package com.tradehero.th.billing;

import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.BillingRequest;
import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.PurchaseOrder;
import com.tradehero.common.billing.exception.BillingException;

/**
 * Created by xavier on 3/13/14.
 */
public class THBillingRequest<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingExceptionType extends BillingException>
        extends BillingRequest<
        ProductIdentifierType,
        ProductDetailType,
        PurchaseOrderType,
        OrderIdType,
        ProductPurchaseType,
        BillingExceptionType>
{
    public static final String TAG = THBillingRequest.class.getSimpleName();

    private PurchaseReporter.OnPurchaseReportedListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> purchaseReportedListener;

    protected THBillingRequest(
            BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> inventoryFetchedListener,
            BillingPurchaseFetcher.OnPurchaseFetchedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseFetchedListener,
            BillingPurchaser.OnPurchaseFinishedListener<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseFinishedListener,
            PurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseReportedListener)
    {
        super(inventoryFetchedListener, purchaseFetchedListener, purchaseFinishedListener);
        this.purchaseReportedListener = purchaseReportedListener;
    }

    //<editor-fold desc="Accessors">
    public PurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> getPurchaseReportedListener()
    {
        return purchaseReportedListener;
    }

    public void setPurchaseReportedListener(
            PurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseReportedListener)
    {
        this.purchaseReportedListener = purchaseReportedListener;
    }
    //</editor-fold>

    public static class THBuilder<
            ProductIdentifierType extends ProductIdentifier,
            ProductDetailType extends ProductDetail<ProductIdentifierType>,
            PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
            OrderIdType extends OrderId,
            ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
            BillingExceptionType extends BillingException>
            extends Builder<
            ProductIdentifierType,
            ProductDetailType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType>
    {
        private PurchaseReporter.OnPurchaseReportedListener<
                ProductIdentifierType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType> purchaseReportedListener;

        public THBuilder()
        {
            super();
        }

        @Override
        public THBillingRequest<ProductIdentifierType, ProductDetailType, PurchaseOrderType, OrderIdType, ProductPurchaseType, BillingExceptionType> build()
        {
            return new THBillingRequest<>(getInventoryFetchedListener(), getPurchaseFetchedListener(), getPurchaseFinishedListener(), purchaseReportedListener);
        }

        //<editor-fold desc="Accessors">
        public PurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> getPurchaseReportedListener()
        {
            return purchaseReportedListener;
        }

        public void setPurchaseReportedListener(
                PurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseReportedListener)
        {
            this.purchaseReportedListener = purchaseReportedListener;
        }
        //</editor-fold>
    }
}
