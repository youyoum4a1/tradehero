package com.tradehero.common.billing;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface BillingActorUser<ProductIdentifierType extends ProductIdentifier,
                        ProductDetailsType extends ProductDetails<ProductIdentifierType>,
                        ExceptionType extends Exception,
                        OrderIdType extends OrderId,
                        ProductPurchaseType extends ProductPurchase<OrderIdType, ProductIdentifierType>,
                        BillingPurchaseHandlerType extends BillingPurchaseHandler<OrderIdType, ProductIdentifierType, ProductPurchaseType, ExceptionType>,
                        BillingActorType extends BillingActor<ProductIdentifierType, ProductDetailsType, ExceptionType, OrderIdType, ProductPurchaseType, BillingPurchaseHandlerType>>
{
    void setBillingActor(BillingActorType billingActor);
    BillingActorType getBillingActor();
}
