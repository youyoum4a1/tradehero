package com.tradehero.common.billing;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface BillingActorUser<
                        ProductIdentifierType extends ProductIdentifier,
                        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
                        OrderIdType extends OrderId,
                        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
                        BillingPurchaseHandlerType extends BillingPurchaseHandler<ProductIdentifierType, OrderIdType, ProductPurchaseType, ExceptionType>,
                        BillingActorType extends BillingActor<
                                                    ProductIdentifierType,
                                                    PurchaseOrderType,
                                                    OrderIdType,
                                                    ProductPurchaseType,
                                                    BillingPurchaseHandlerType,
                                                    ExceptionType>,
                        ExceptionType extends Exception>
{
    void setBillingActor(BillingActorType billingActor);
    BillingActorType getBillingActor();
}
