package com.tradehero.th.models.user;

import android.content.Context;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.user.follow.SimpleFollowUserAssistant;
import com.tradehero.th.network.service.UserServiceWrapper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class OpenSimpleFollowUserAssistant extends SimpleFollowUserAssistant
{
    //<editor-fold desc="Constructors">
    public OpenSimpleFollowUserAssistant(
            @NonNull Context context,
            @NonNull UserBaseKey heroId,
            @Nullable OnUserFollowedListener userFollowedListener)
    {
        super(context, heroId, userFollowedListener);
    }
    //</editor-fold>

    public void setUserServiceWrapper(UserServiceWrapper userServiceWrapper)
    {
        this.userServiceWrapper = userServiceWrapper;
    }

    @Override public void launchPremiumFollow()
    {
        super.launchPremiumFollow();
    }

    @Override public void notifyFollowSuccess(@NonNull UserBaseKey userToFollow, @NonNull UserProfileDTO currentUserProfile)
    {
        super.notifyFollowSuccess(userToFollow, currentUserProfile);
    }

    @Override public void notifyFollowFailed(@NonNull UserBaseKey userToFollow, @NonNull Throwable error)
    {
        super.notifyFollowFailed(userToFollow, error);
    }
}
