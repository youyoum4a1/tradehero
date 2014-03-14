package com.tradehero.th.billing;

import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.BillingRequest;
import com.tradehero.common.billing.OnBillingAvailableListener;
import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductIdentifierFetcher;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.PurchaseOrder;
import com.tradehero.common.billing.exception.BillingException;
import java.util.List;

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

    //<editor-fold desc="Listeners">
    private PurchaseReporter.OnPurchaseReportedListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> purchaseReportedListener;
    //</editor-fold>

    private ProductPurchaseType purchaseToReport;

    protected THBillingRequest(
            OnBillingAvailableListener<BillingExceptionType> billingAvailableListener,
            ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierType, BillingExceptionType> productIdentifierFetchedListener,
            BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> inventoryFetchedListener,
            BillingPurchaseFetcher.OnPurchaseFetchedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseFetchedListener,
            BillingPurchaser.OnPurchaseFinishedListener<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseFinishedListener,
            PurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseReportedListener,
            Boolean billingAvailable,
            Boolean fetchProductIdentifiers,
            List<ProductIdentifierType> productIdentifiersForInventory,
            Boolean fetchPurchase,
            PurchaseOrderType purchaseOrder,
            ProductPurchaseType purchaseToReport)
    {
        super(
                billingAvailableListener,
                productIdentifierFetchedListener,
                inventoryFetchedListener,
                purchaseFetchedListener,
                purchaseFinishedListener,
                billingAvailable,
                fetchProductIdentifiers,
                productIdentifiersForInventory,
                fetchPurchase,
                purchaseOrder);
        this.purchaseReportedListener = purchaseReportedListener;
        this.purchaseToReport = purchaseToReport;
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

    public ProductPurchaseType getPurchaseToReport()
    {
        return purchaseToReport;
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
        //<editor-fold desc="Listeners">
        private PurchaseReporter.OnPurchaseReportedListener<
                ProductIdentifierType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType> purchaseReportedListener;
        //</editor-fold>

        private ProductPurchaseType purchaseToReport;

        public THBuilder()
        {
            super();
        }

        @Override
        public THBillingRequest<ProductIdentifierType, ProductDetailType, PurchaseOrderType, OrderIdType, ProductPurchaseType, BillingExceptionType> build()
        {
            return new THBillingRequest<>(
                    getBillingAvailableListener(),
                    getProductIdentifierFetchedListener(),
                    getInventoryFetchedListener(),
                    getPurchaseFetchedListener(),
                    getPurchaseFinishedListener(),
                    purchaseReportedListener,
                    getBillingAvailable(),
                    getFetchProductIdentifiers(),
                    getProductIdentifiersForInventory(),
                    getFetchPurchase(),
                    getPurchaseOrder(),
                    purchaseToReport);
        }

        @Override protected List<Object> getTests()
        {
            List<Object> tests = super.getTests();
            tests.add(purchaseToReport);
            return tests;
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

        public ProductPurchaseType getPurchaseToReport()
        {
            return purchaseToReport;
        }

        public void setPurchaseToReport(ProductPurchaseType purchaseToReport)
        {
            this.purchaseToReport = purchaseToReport;
        }
        //</editor-fold>
    }
}
