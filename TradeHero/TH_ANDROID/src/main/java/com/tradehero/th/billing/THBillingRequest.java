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
import com.tradehero.th.api.users.UserBaseKey;
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

    private OnFollowResultListener followResultListener;
    //</editor-fold>

    private ProductPurchaseType purchaseToReport;
    private UserBaseKey userToFollow;

    protected THBillingRequest(
            OnBillingAvailableListener<BillingExceptionType> billingAvailableListener,
            ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierType, BillingExceptionType> productIdentifierFetchedListener,
            BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> inventoryFetchedListener,
            BillingPurchaseFetcher.OnPurchaseFetchedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseFetchedListener,
            BillingPurchaser.OnPurchaseFinishedListener<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseFinishedListener,
            PurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseReportedListener,
            OnFollowResultListener followResultListener,
            Boolean billingAvailable,
            Boolean fetchProductIdentifiers,
            Boolean fetchInventory,
            List<ProductIdentifierType> productIdentifiersForInventory,
            Boolean fetchPurchase,
            PurchaseOrderType purchaseOrder,
            ProductPurchaseType purchaseToReport,
            UserBaseKey userToFollow)
    {
        super(
                billingAvailableListener,
                productIdentifierFetchedListener,
                inventoryFetchedListener,
                purchaseFetchedListener,
                purchaseFinishedListener,
                billingAvailable,
                fetchProductIdentifiers,
                fetchInventory,
                productIdentifiersForInventory,
                fetchPurchase,
                purchaseOrder);
        this.purchaseReportedListener = purchaseReportedListener;
        this.followResultListener = followResultListener;
        this.purchaseToReport = purchaseToReport;
        this.userToFollow = userToFollow;
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

    public UserBaseKey getUserToFollow()
    {
        return userToFollow;
    }

    public void setUserToFollow(UserBaseKey userToFollow)
    {
        this.userToFollow = userToFollow;
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

        private OnFollowResultListener followResultListener;
        //</editor-fold>

        private ProductPurchaseType purchaseToReport;
        private UserBaseKey userToFollow;

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
                    followResultListener,
                    getBillingAvailable(),
                    getFetchProductIdentifiers(),
                    getFetchInventory(),
                    getProductIdentifiersForInventory(),
                    getFetchPurchase(),
                    getPurchaseOrder(),
                    purchaseToReport,
                    userToFollow);
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

        public OnFollowResultListener getFollowResultListener()
        {
            return followResultListener;
        }

        public void setFollowResultListener(OnFollowResultListener followResultListener)
        {
            this.followResultListener = followResultListener;
        }

        public ProductPurchaseType getPurchaseToReport()
        {
            return purchaseToReport;
        }

        public void setPurchaseToReport(ProductPurchaseType purchaseToReport)
        {
            this.purchaseToReport = purchaseToReport;
        }

        public UserBaseKey getUserToFollow()
        {
            return userToFollow;
        }

        public void setUserToFollow(UserBaseKey userToFollow)
        {
            this.userToFollow = userToFollow;
        }
        //</editor-fold>
    }
}
