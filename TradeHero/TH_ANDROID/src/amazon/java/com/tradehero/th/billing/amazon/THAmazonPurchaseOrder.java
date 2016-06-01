package com.ayondo.academy.billing.amazon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.billing.amazon.AmazonPurchaseOrder;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.ayondo.academy.api.portfolio.OwnedPortfolioId;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.billing.THPurchaseOrder;

public class THAmazonPurchaseOrder extends AmazonPurchaseOrder<AmazonSKU>
    implements THPurchaseOrder<AmazonSKU>
{
    @NonNull private final OwnedPortfolioId applicablePortfolioId;
    @Nullable private UserBaseKey userToFollow;

    //<editor-fold desc="Constructors">
    public THAmazonPurchaseOrder(
            @NonNull AmazonSKU sku,
            int quantity,
            @NonNull OwnedPortfolioId applicablePortfolioId)
    {
        this(sku, quantity, applicablePortfolioId, null);
    }

    public THAmazonPurchaseOrder(
            @NonNull AmazonSKU sku,
            int quantity,
            @NonNull OwnedPortfolioId applicablePortfolioId,
            @Nullable UserBaseKey userToFollow)
    {
        super(sku, quantity);
        this.applicablePortfolioId = applicablePortfolioId;
        this.userToFollow = userToFollow;
    }
    //</editor-fold>

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
