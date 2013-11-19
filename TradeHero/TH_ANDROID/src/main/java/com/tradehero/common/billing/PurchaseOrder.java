package com.tradehero.common.billing;

/** Created with IntelliJ IDEA. User: xavier Date: 11/19/13 Time: 10:39 AM To change this template use File | Settings | File Templates. */
public interface PurchaseOrder<ProductIdentifierType extends ProductIdentifier>
{
    ProductIdentifierType getProductIdentifier();
    int getQuantity();
}
