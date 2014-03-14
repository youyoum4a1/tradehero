package com.tradehero.th.billing;

import com.tradehero.common.billing.BillingInteractor;
import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.PurchaseOrder;
import com.tradehero.common.billing.exception.BillingException;

/**
 * Created by xavier on 2/24/14.
 */
public interface THBillingInteractor<
            ProductIdentifierType extends ProductIdentifier,
            ProductDetailType extends ProductDetail<ProductIdentifierType>,
            PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
            OrderIdType extends OrderId,
            ProductPurchaseType extends ProductPurchase<
                    ProductIdentifierType,
                    OrderIdType>,
            THBillingLogicHolderType extends THBillingLogicHolder<
                    ProductIdentifierType,
                    ProductDetailType,
                    PurchaseOrderType,
                    OrderIdType,
                    ProductPurchaseType,
                    THBillingRequestType,
                    BillingExceptionType>,
            THBillingRequestType extends THBillingRequest<
                ProductIdentifierType,
                ProductDetailType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType>,
            BillingExceptionType extends BillingException>
        extends BillingInteractor<
            ProductIdentifierType,
            ProductDetailType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType,
            THBillingLogicHolderType,
            THBillingRequestType,
            BillingExceptionType>
{
    static final String DOMAIN_VIRTUAL_DOLLAR = "virtualDollar";
    static final String DOMAIN_FOLLOW_CREDITS = "followCredits";
    static final String DOMAIN_STOCK_ALERTS = "stockAlerts";
    static final String DOMAIN_RESET_PORTFOLIO = "resetPortfolio";

    THBillingLogicHolderType getTHBillingLogicHolder();

    //void registerFollowRequestedListener(OnFollowResultListener followRequestedListener);
    //void followHero(UserBaseKey userBaseKey);
}
