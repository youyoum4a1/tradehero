package com.tradehero.th.billing.request;

import com.tradehero.common.billing.BaseProductIdentifierList;
import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductIdentifierListKey;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.request.BillingRequest;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.billing.THPurchaseReporter;
import com.tradehero.th.billing.THPurchaseOrder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class THBillingRequest<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        THPurchaseOrderType extends THPurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingExceptionType extends BillingException>
        extends BillingRequest<
        ProductIdentifierListKeyType,
        ProductIdentifierType,
        ProductIdentifierListType,
        ProductDetailType,
        THPurchaseOrderType,
        OrderIdType,
        ProductPurchaseType,
        BillingExceptionType>
{
    public boolean reportPurchase;
    public ProductPurchaseType purchaseToReport;
    public final UserBaseKey userToFollow;
    @Nullable public THPurchaseReporter.OnPurchaseReportedListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> purchaseReportedListener;

    //<editor-fold desc="Constructors">
    protected THBillingRequest(@NotNull Builder<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType,
            ProductDetailType,
            THPurchaseOrderType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType,
            ?> builder)
    {
        super(builder);
        this.reportPurchase = builder.reportPurchase;
        this.purchaseToReport = builder.purchaseToReport;
        this.userToFollow = builder.userToFollow;
        this.purchaseReportedListener = builder.purchaseReportedListener;
    }
    //</editor-fold>

    public static abstract class Builder<
            ProductIdentifierListKeyType extends ProductIdentifierListKey,
            ProductIdentifierType extends ProductIdentifier,
            ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
            ProductDetailType extends ProductDetail<ProductIdentifierType>,
            THPurchaseOrderType extends THPurchaseOrder<ProductIdentifierType>,
            OrderIdType extends OrderId,
            ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
            BillingExceptionType extends BillingException,
            BuilderType extends Builder<
                    ProductIdentifierListKeyType,
                    ProductIdentifierType,
                    ProductIdentifierListType,
                    ProductDetailType,
                    THPurchaseOrderType,
                    OrderIdType,
                    ProductPurchaseType,
                    BillingExceptionType,
                    BuilderType>>
            extends BillingRequest.Builder<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType,
            ProductDetailType,
            THPurchaseOrderType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType,
            BuilderType>
    {
        //<editor-fold desc="Constructors">
        protected Builder()
        {
        }
        //</editor-fold>

        //<editor-fold desc="Whether to Report Purchase">
        private boolean reportPurchase;
        private ProductPurchaseType purchaseToReport;
        private UserBaseKey userToFollow;
        @Nullable private THPurchaseReporter.OnPurchaseReportedListener<
                ProductIdentifierType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType> purchaseReportedListener;

        public BuilderType reportPurchase(boolean reportPurchase)
        {
            this.reportPurchase = reportPurchase;
            return self();
        }

        public BuilderType purchaseToReport(
                ProductPurchaseType purchaseToReport)
        {
            this.purchaseToReport = purchaseToReport;
            return self();
        }

        public BuilderType userToPremiumFollow(UserBaseKey userToFollow)
        {
            this.userToFollow = userToFollow;
            return self();
        }

        public BuilderType purchaseReportedListener(
                @Nullable THPurchaseReporter.OnPurchaseReportedListener<
                        ProductIdentifierType,
                        OrderIdType,
                        ProductPurchaseType,
                        BillingExceptionType> purchaseReportedListener)
        {
            this.purchaseReportedListener = purchaseReportedListener;
            return self();
        }
        //</editor-fold>

        @Override public THBillingRequest<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                ProductDetailType,
                THPurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType> build()
        {
            return new THBillingRequest<>(this);
        }
    }

    @Override public void onDestroy()
    {
        this.purchaseReportedListener = null;
        super.onDestroy();
    }

    @Override public String toString()
    {
        return "THBillingRequest:{" +
                super.toString() +
                ", reportPurchase=" + reportPurchase +
                ", purchaseToReport=" + purchaseToReport +
                ", userToFollow=" + userToFollow +
                ", purchaseReportedListener=" + purchaseReportedListener +
                '}';
    }
}
