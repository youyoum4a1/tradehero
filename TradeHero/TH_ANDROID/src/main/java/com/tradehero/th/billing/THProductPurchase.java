package com.tradehero.th.billing;

import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.th.api.billing.PurchaseReportDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
