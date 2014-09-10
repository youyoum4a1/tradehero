package com.tradehero.th.billing;

import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.PurchaseOrder;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface THPurchaseOrder<ProductIdentifierType extends ProductIdentifier>
    extends PurchaseOrder<ProductIdentifierType>
{
    void setApplicablePortfolioId(@NotNull OwnedPortfolioId applicablePortfolioId);
    @NotNull OwnedPortfolioId getApplicablePortfolioId();
    void setUserToFollow(@Nullable UserBaseKey userToFollow);
    @Nullable UserBaseKey getUserToFollow();
}
