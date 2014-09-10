package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.user.follow.SimpleFollowUserAssistant;
import com.tradehero.th.network.service.UserServiceWrapper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OpenSimpleFollowUserAssistant extends SimpleFollowUserAssistant
{
    //<editor-fold desc="Constructors">
    public OpenSimpleFollowUserAssistant(
            @NotNull UserBaseKey heroId,
            @Nullable OnUserFollowedListener userFollowedListener)
    {
        super(heroId, userFollowedListener);
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

    @Override public void notifyFollowSuccess(@NotNull UserBaseKey userToFollow, @NotNull UserProfileDTO currentUserProfile)
    {
        super.notifyFollowSuccess(userToFollow, currentUserProfile);
    }

    @Override public void notifyFollowFailed(@NotNull UserBaseKey userToFollow, @NotNull Throwable error)
    {
        super.notifyFollowFailed(userToFollow, error);
    }
}
