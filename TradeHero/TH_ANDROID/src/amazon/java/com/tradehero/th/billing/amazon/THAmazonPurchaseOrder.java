package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonPurchaseOrder;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.billing.THPurchaseOrder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class THAmazonPurchaseOrder extends AmazonPurchaseOrder<AmazonSKU>
    implements THPurchaseOrder<AmazonSKU>
{
    @NonNull private OwnedPortfolioId applicablePortfolioId;
    @Nullable private UserBaseKey userToFollow;

    //<editor-fold desc="Constructors">
    public THAmazonPurchaseOrder(
            @NonNull AmazonSKU sku,
            int quantity,
            @NonNull OwnedPortfolioId applicablePortfolioId)
    {
        super(sku, quantity);
        this.applicablePortfolioId = applicablePortfolioId;
    }
    //</editor-fold>

    @Override public void setApplicablePortfolioId(@NonNull OwnedPortfolioId applicablePortfolioId)
    {
        this.applicablePortfolioId = applicablePortfolioId;
    }

    @NonNull @Override public OwnedPortfolioId getApplicablePortfolioId()
    {
        return applicablePortfolioId;
    }

    @Override public void setUserToFollow(@Nullable UserBaseKey userToFollow)
    {
        this.userToFollow = userToFollow;
    }

    @Nullable @Override public UserBaseKey getUserToFollow()
    {
        return userToFollow;
    }

    @Override public String toString()
    {
        return "THAmazonPurchaseOrder{" +
                super.toString() +
                ", applicablePortfolioId=" + applicablePortfolioId +
                ", userToFollow=" + userToFollow +
                '}';
    }
}
