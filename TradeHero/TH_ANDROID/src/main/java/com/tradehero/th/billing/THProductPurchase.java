package com.tradehero.th.billing;

import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;

/**
 * Created by xavier on 3/24/14.
 */
public interface THProductPurchase<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId>
    extends ProductPurchase<ProductIdentifierType, OrderIdType>
{
    void setUserToFollow(UserBaseKey userToFollow);
    UserBaseKey getUserToFollow();
    OwnedPortfolioId getApplicableOwnedPortfolioId();
}
