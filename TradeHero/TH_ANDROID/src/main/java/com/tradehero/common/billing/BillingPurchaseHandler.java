package com.tradehero.common.billing;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 12:16 PM To change this template use File | Settings | File Templates. */
public interface BillingPurchaseHandler<OrderIdType extends OrderId,
                                    ProductIdentifierType extends ProductIdentifier,
                                    ProductPurchaseType extends ProductPurchase<OrderIdType, ProductIdentifierType>,
                                    ExceptionType extends Exception>
{
    void handlePurchaseReceived(int requestCode, ProductPurchaseType purchase);
    void handlePurchaseException(int requestCode, ExceptionType exception);
}
