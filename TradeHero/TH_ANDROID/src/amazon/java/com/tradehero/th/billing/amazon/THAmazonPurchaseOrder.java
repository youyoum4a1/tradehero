package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonPurchaseOrder;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.billing.THPurchaseOrder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class THAmazonPurchaseOrder extends AmazonPurchaseOrder<AmazonSKU>
    implements THPurchaseOrder<AmazonSKU>
{
    @NotNull private OwnedPortfolioId applicablePortfolioId;
    @Nullable private UserBaseKey userToFollow;

    //<editor-fold desc="Constructors">
    public THAmazonPurchaseOrder(
            @NotNull AmazonSKU sku,
            int quantity,
            @NotNull OwnedPortfolioId applicablePortfolioId)
    {
        super(sku, quantity);
        this.applicablePortfolioId = applicablePortfolioId;
    }
    //</editor-fold>

    @Override public void setApplicablePortfolioId(@NotNull OwnedPortfolioId applicablePortfolioId)
    {
        this.applicablePortfolioId = applicablePortfolioId;
    }

    @NotNull @Override public OwnedPortfolioId getApplicablePortfolioId()
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
}
