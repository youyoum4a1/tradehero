package com.androidth.general.billing;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.common.billing.OrderId;
import com.androidth.general.common.billing.ProductIdentifier;
import com.androidth.general.common.billing.ProductPurchase;
import com.androidth.general.api.billing.PurchaseReportDTO;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.users.UserBaseKey;

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
