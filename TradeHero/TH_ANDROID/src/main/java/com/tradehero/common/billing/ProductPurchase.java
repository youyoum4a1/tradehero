package com.tradehero.common.billing;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 11:24 AM To change this template use File | Settings | File Templates. */
public interface ProductPurchase<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId>
{
    OrderIdType getOrderId();
    ProductIdentifierType getProductIdentifier();
}
