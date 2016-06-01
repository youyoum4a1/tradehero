package com.ayondo.academy.billing;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;
import com.ayondo.academy.api.billing.PurchaseReportDTO;
import com.ayondo.academy.api.portfolio.OwnedPortfolioId;
import com.ayondo.academy.api.users.UserBaseKey;

public interface THProductPurchase<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId>
    extends ProductPurchase<ProductIdentifierType, OrderIdType>
{
    void setUserToFollow(@Nullable UserBaseKey userToFollow);
    @Nullable UserBaseKey getUserToFollow();
    @NonNull OwnedPortfolioId getApplicableOwnedPortfolioId();
    @NonNull PurchaseReportDTO getPurchaseReportDTO();
}
