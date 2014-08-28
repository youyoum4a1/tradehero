package com.tradehero.th.models.user;

import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.models.user.follow.FollowUserAssistant;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OpenFollowUserAssistant extends FollowUserAssistant
{
    //<editor-fold desc="Constructors">
    public OpenFollowUserAssistant(
            @NotNull UserBaseKey heroId,
            @Nullable OnUserFollowedListener userFollowedListener,
            @NotNull OwnedPortfolioId applicablePortfolioId)
    {
        super(heroId, userFollowedListener, applicablePortfolioId);
    }
    //</editor-fold>

    public Integer getRequestCode()
    {
        return requestCode;
    }

    public void setUserProfileCache(UserProfileCache userProfileCache)
    {
        this.userProfileCache = userProfileCache;
    }

    public void setUserServiceWrapper(UserServiceWrapper userServiceWrapper)
    {
        this.userServiceWrapper = userServiceWrapper;
    }

    public void setBillingInteractor(THBillingInteractor billingInteractor)
    {
        this.billingInteractor = billingInteractor;
    }
}
