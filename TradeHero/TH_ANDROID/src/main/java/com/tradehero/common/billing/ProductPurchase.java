package com.tradehero.common.billing;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 11:24 AM To change this template use File | Settings | File Templates. */
public interface ProductPurchase<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId>
    extends DTO
{
    OrderIdType getOrderId();
    ProductIdentifierType getProductIdentifier();
    OwnedPortfolioId getApplicableOwnedPortfolioId();
}
