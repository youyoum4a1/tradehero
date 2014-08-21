package com.tradehero.common.billing;

import org.jetbrains.annotations.NotNull;

public interface PurchaseOrder<ProductIdentifierType extends ProductIdentifier>
{
    @NotNull ProductIdentifierType getProductIdentifier();
    int getQuantity();
}
