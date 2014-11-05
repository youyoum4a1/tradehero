package com.tradehero.common.billing;

import com.tradehero.common.persistence.DTO;

import android.support.annotation.NonNull;

public interface ProductPurchase<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId>
    extends DTO
{
    @NonNull OrderIdType getOrderId();
    @NonNull ProductIdentifierType getProductIdentifier();
}
