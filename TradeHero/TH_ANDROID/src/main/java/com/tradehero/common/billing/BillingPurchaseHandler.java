package com.tradehero.common.billing;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 12:16 PM To change this template use File | Settings | File Templates. */
public interface BillingPurchaseHandler<
                                    ProductIdentifierType extends ProductIdentifier,
                                    OrderIdType extends OrderId,
                                    ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
                                    ExceptionType extends Exception>
{
    void handlePurchaseReceived(int requestCode, ProductPurchaseType purchase);
    void handlePurchaseException(int requestCode, ExceptionType exception);
}
