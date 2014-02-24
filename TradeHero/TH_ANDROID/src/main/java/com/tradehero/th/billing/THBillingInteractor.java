package com.tradehero.th.billing;

import com.tradehero.common.billing.BillingInteractor;
import com.tradehero.common.billing.BillingLogicHolder;
import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.PurchaseOrder;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.th.api.users.UserBaseKey;

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
            BillingLogicHolderType extends BillingLogicHolder<
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
            BillingLogicHolderType,
            BillingExceptionType>
{
    void registerFollowRequestedListener(OnFollowResultListener followRequestedListener);
    void followHero(UserBaseKey userBaseKey);
}
