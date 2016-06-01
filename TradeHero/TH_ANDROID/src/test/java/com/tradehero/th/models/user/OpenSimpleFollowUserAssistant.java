package com.ayondo.academy.models.user;

import android.content.Context;
import android.support.annotation.NonNull;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.models.user.follow.FollowUserAssistant;
import com.ayondo.academy.network.service.UserServiceWrapper;
import rx.Observable;

public class OpenSimpleFollowUserAssistant extends FollowUserAssistant
{
    //<editor-fold desc="Constructors">
    public OpenSimpleFollowUserAssistant(
            @NonNull Context context,
            @NonNull UserBaseKey heroId)
    {
        super(context, heroId);
    }
    //</editor-fold>

    public void setUserServiceWrapper(UserServiceWrapper userServiceWrapper)
    {
        this.userServiceWrapper = userServiceWrapper;
    }

    @Override @NonNull public Observable<UserProfileDTO> launchPremiumFollowRx()
    {
        return super.launchPremiumFollowRx();
    }
}
