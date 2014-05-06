package com.tradehero.common.billing;

import com.tradehero.common.persistence.DTO;


public interface ProductPurchase<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId>
    extends DTO
{
    OrderIdType getOrderId();
    ProductIdentifierType getProductIdentifier();
}
