package com.ayondo.academy.billing;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.PurchaseOrder;
import com.ayondo.academy.api.portfolio.OwnedPortfolioId;
import com.ayondo.academy.api.users.UserBaseKey;

public interface THPurchaseOrder<ProductIdentifierType extends ProductIdentifier>
    extends PurchaseOrder<ProductIdentifierType>
{
    @NonNull OwnedPortfolioId getApplicablePortfolioId();
    void setUserToFollow(@Nullable UserBaseKey userToFollow);
    @Nullable UserBaseKey getUserToFollow();
}
