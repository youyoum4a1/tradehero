package com.tradehero.common.billing;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;


public interface ProductPurchase<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId>
    extends DTO
{
    OrderIdType getOrderId();
    ProductIdentifierType getProductIdentifier();
}
