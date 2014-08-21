package com.tradehero.th.billing.request;

import com.tradehero.common.billing.BaseProductIdentifierList;
import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductIdentifierListKey;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.request.BaseUIBillingRequest;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THPurchaseOrder;
import com.tradehero.th.billing.THPurchaseReporter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class BaseTHUIBillingRequest<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        THPurchaseOrderType extends THPurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingExceptionType extends BillingException>
        extends BaseUIBillingRequest<
        ProductIdentifierListKeyType,
        ProductIdentifierType,
        ProductIdentifierListType,
        ProductDetailType,
        THPurchaseOrderType,
        OrderIdType,
        ProductPurchaseType,
        BillingExceptionType>
        implements THUIBillingRequest<
        ProductIdentifierListKeyType,
        ProductIdentifierType,
        ProductIdentifierListType,
        ProductDetailType,
        THPurchaseOrderType,
        OrderIdType,
        ProductPurchaseType,
        BillingExceptionType>
{
    //<editor-fold desc="Product Identifiers To Present">
    /**
     * The domain of product identifiers to present to the user.
     */
    private ProductIdentifierDomain domainToPresent;

    @Override public ProductIdentifierDomain getDomainToPresent()
    {
        return domainToPresent;
    }

    @Override public void setDomainToPresent(ProductIdentifierDomain domainToPresent)
    {
        this.domainToPresent = domainToPresent;
    }
    //</editor-fold>

    //<editor-fold desc="Purchase Reporting">
    private boolean reportPurchase;

    @Override public boolean getReportPurchase()
    {
        return reportPurchase;
    }

    @Override public void setReportPurchase(boolean reportPurchase)
    {
        this.reportPurchase = reportPurchase;
    }

    /**
     * The portfolio to pass when making a purchase
     */
    @NotNull private final OwnedPortfolioId applicablePortfolioId; // TODO Move out

    @Override @NotNull public OwnedPortfolioId getApplicablePortfolioId()
    {
        return applicablePortfolioId;
    }

    /**
     * Indicates whether we want the Interactor to pop a dialog when reporting fails
     */
    public final boolean popIfReportFailed;

    @Override public boolean getPopIfReportFailed()
    {
        return popIfReportFailed;
    }

    @Nullable private THPurchaseReporter.OnPurchaseReportedListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> purchaseReportedListener;

    @Nullable @Override public THPurchaseReporter.OnPurchaseReportedListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> getPurchaseReportedListener()
    {
        return purchaseReportedListener;
    }

    @Override public void setPurchaseReportedListener(
            @Nullable THPurchaseReporter.OnPurchaseReportedListener<
                    ProductIdentifierType,
                    OrderIdType,
                    ProductPurchaseType,
                    BillingExceptionType> purchaseReportedListener)
    {
        this.purchaseReportedListener = purchaseReportedListener;
    }
    //</editor-fold>

    //<editor-fold desc="User Following">
    private UserBaseKey userToPremiumFollow;

    @Override public UserBaseKey getUserToPremiumFollow()
    {
        return userToPremiumFollow;
    }

    @Override public void setUserToPremiumFollow(UserBaseKey userToPremiumFollow)
    {
        this.userToPremiumFollow = userToPremiumFollow;
    }
    //</editor-fold>

    //<editor-fold desc="Constructors">
    protected BaseTHUIBillingRequest(
            @NotNull BaseTHUIBillingRequest.Builder<
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

        this.domainToPresent = builder.domainToPresent;

        this.reportPurchase = builder.reportPurchase;
        this.applicablePortfolioId = builder.applicablePortfolioId;
        this.popIfReportFailed = builder.popIfReportFailed;
        this.purchaseReportedListener = builder.purchaseReportedListener;

        this.userToPremiumFollow = builder.userToPremiumFollow;
    }
    //</editor-fold>

    @Override public void onDestroy()
    {
        purchaseReportedListener = null;
        super.onDestroy();
    }

    @Override abstract public THBillingRequest.Builder<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType,
            ProductDetailType,
            THPurchaseOrderType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType,
            ?> createEmptyBillingRequestBuilder();

    //<editor-fold desc="Builder">
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
            extends BaseUIBillingRequest.Builder<
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
        //<editor-fold desc="Product Identifiers To Present">
        private ProductIdentifierDomain domainToPresent;

        public BuilderType domainToPresent(ProductIdentifierDomain domainToPresent)
        {
            this.domainToPresent = domainToPresent;
            return self();
        }
        //</editor-fold>

        //<editor-fold desc="Purchase Reporting">
        private boolean reportPurchase;
        @NotNull private OwnedPortfolioId applicablePortfolioId;

        private boolean popIfReportFailed;
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

        public BuilderType applicablePortfolioId(@NotNull OwnedPortfolioId applicablePortfolioId)
        {
            this.applicablePortfolioId = applicablePortfolioId;
            return self();
        }

        public BuilderType popIfReportFailed(boolean popIfReportFailed)
        {
            this.popIfReportFailed = popIfReportFailed;
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

        //<editor-fold desc="User Following">
        private UserBaseKey userToPremiumFollow;

        public BuilderType userToPremiumFollow(UserBaseKey userToPremiumFollow)
        {
            this.userToPremiumFollow = userToPremiumFollow;
            return self();
        }
        //</editor-fold>

        @Override abstract protected BuilderType self();
        @Override abstract public BaseTHUIBillingRequest<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                ProductDetailType,
                THPurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType> build();
    }
    //</editor-fold>

    @Override public String toString()
    {
        return "BaseTHUIBillingRequest:{" +
                super.toString() +
                ", domainToPresent=" + domainToPresent +
                ", reportPurchase=" + reportPurchase +
                ", applicablePortfolioId=" + applicablePortfolioId +
                ", popIfReportFailed=" + popIfReportFailed +
                ", purchaseReportedListener=" + purchaseReportedListener +
                ", userToPremiumFollow=" + userToPremiumFollow +
                '}';
    }
}
