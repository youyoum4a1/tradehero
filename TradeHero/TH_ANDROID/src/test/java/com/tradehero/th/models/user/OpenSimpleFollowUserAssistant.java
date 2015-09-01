package com.tradehero.th.models.user;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.user.follow.FollowUserAssistant;
import com.tradehero.th.network.service.UserServiceWrapper;
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
