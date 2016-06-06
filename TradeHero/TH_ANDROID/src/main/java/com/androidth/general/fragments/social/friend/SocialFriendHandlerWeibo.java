package com.androidth.general.fragments.social.friend;

import android.support.annotation.NonNull;
import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.social.InviteFormWeiboDTO;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.network.service.UserServiceWrapper;
import javax.inject.Inject;
import rx.Observable;

public class SocialFriendHandlerWeibo extends SocialFriendHandler
{
    //<editor-fold desc="Constructors">
    @Inject public SocialFriendHandlerWeibo(@NonNull UserServiceWrapper userService)
    {
        super(userService);
    }
    //</editor-fold>

    public Observable<BaseResponseDTO> inviteWeiboFriends(
            @NonNull String msg,
            @NonNull UserBaseKey userKey)
    {
        return inviteFriends(userKey, new InviteFormWeiboDTO(msg));
    }
}
