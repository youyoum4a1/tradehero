package com.tradehero.common.billing;

public interface PurchaseOrder<ProductIdentifierType extends ProductIdentifier>
{
    ProductIdentifierType getProductIdentifier();
    int getQuantity();
}
