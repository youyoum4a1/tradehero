package com.tradehero.th.billing.request;

import com.tradehero.common.billing.BaseProductIdentifierList;
import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductIdentifierListKey;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.request.UIBillingRequest;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THPurchaseOrder;
import com.tradehero.th.billing.THPurchaseReporter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface THUIBillingRequest<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        THPurchaseOrderType extends THPurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingExceptionType extends BillingException>
        extends UIBillingRequest<
        ProductIdentifierListKeyType,
        ProductIdentifierType,
        ProductIdentifierListType,
        ProductDetailType,
        THPurchaseOrderType,
        OrderIdType,
        ProductPurchaseType,
        BillingExceptionType>
{
    //<editor-fold desc="Generics">
    @NotNull OwnedPortfolioId getApplicablePortfolioId();
    //</editor-fold>

    //<editor-fold desc="Product Identifiers To Present">
    ProductIdentifierDomain getDomainToPresent();
    void setDomainToPresent(ProductIdentifierDomain domainToPresent);
    //</editor-fold>

    //<editor-fold desc="Reporting Purchase">
    boolean getReportPurchase();
    void setReportPurchase(boolean reportPurchase);
    boolean getPopIfReportFailed();
    @Nullable THPurchaseReporter.OnPurchaseReportedListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> getPurchaseReportedListener();
    void setPurchaseReportedListener(
            @Nullable THPurchaseReporter.OnPurchaseReportedListener<
                    ProductIdentifierType,
                    OrderIdType,
                    ProductPurchaseType,
                    BillingExceptionType> purchaseReportedListener);
    //</editor-fold>

    //<editor-fold desc="Premium Following User">
    UserBaseKey getUserToPremiumFollow();
    void setUserToPremiumFollow(UserBaseKey userToPremiumFollow);
    //</editor-fold>
}
