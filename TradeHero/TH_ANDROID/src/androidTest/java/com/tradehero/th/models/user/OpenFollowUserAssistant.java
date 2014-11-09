package com.tradehero.th.models.user;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.models.user.follow.FollowUserAssistant;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCacheRx;

public class OpenFollowUserAssistant extends FollowUserAssistant
{
    //<editor-fold desc="Constructors">
    public OpenFollowUserAssistant(
            @NonNull Context context,
            @NonNull UserBaseKey heroId,
            @Nullable OnUserFollowedListener userFollowedListener,
            @NonNull OwnedPortfolioId applicablePortfolioId)
    {
        super(context, heroId, userFollowedListener, applicablePortfolioId);
    }
    //</editor-fold>

    public Integer getRequestCode()
    {
        return requestCode;
    }

    public void setUserProfileCache(UserProfileCacheRx userProfileCache)
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
