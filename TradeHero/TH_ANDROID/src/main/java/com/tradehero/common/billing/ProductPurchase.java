package com.tradehero.common.billing;

import com.tradehero.common.persistence.DTO;

import org.jetbrains.annotations.NotNull;

public interface ProductPurchase<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId>
    extends DTO
{
    @NotNull OrderIdType getOrderId();
    @NotNull ProductIdentifierType getProductIdentifier();
}
