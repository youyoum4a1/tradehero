package com.androidth.general.billing;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.common.billing.ProductIdentifier;
import com.androidth.general.common.billing.PurchaseOrder;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.users.UserBaseKey;

public interface THPurchaseOrder<ProductIdentifierType extends ProductIdentifier>
    extends PurchaseOrder<ProductIdentifierType>
{
    @NonNull OwnedPortfolioId getApplicablePortfolioId();
    void setUserToFollow(@Nullable UserBaseKey userToFollow);
    @Nullable UserBaseKey getUserToFollow();
}
