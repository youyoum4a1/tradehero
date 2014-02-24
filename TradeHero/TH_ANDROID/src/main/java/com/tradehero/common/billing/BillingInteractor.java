package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface BillingInteractor<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<
                ProductIdentifierType,
                OrderIdType>,
        BillingPurchaseFinishedListenerType extends BillingPurchaser.OnPurchaseFinishedListener<
                ProductIdentifierType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType>,
        BillingLogicHolderType extends BillingLogicHolder<
                        ProductIdentifierType,
                        ProductDetailType,
                        PurchaseOrderType,
                        OrderIdType,
                        ProductPurchaseType,
                        BillingPurchaseFinishedListenerType,
                        BillingExceptionType>,
        BillingExceptionType extends BillingException>
{
    void setBillingLogicHolder(BillingLogicHolderType billingActor);
    BillingLogicHolderType getBillingLogicHolder();
}
