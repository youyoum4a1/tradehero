package com.tradehero.common.billing;

import android.support.annotation.NonNull;

public interface PurchaseOrder<ProductIdentifierType extends ProductIdentifier>
{
    @NonNull ProductIdentifierType getProductIdentifier();
    int getQuantity();
}
