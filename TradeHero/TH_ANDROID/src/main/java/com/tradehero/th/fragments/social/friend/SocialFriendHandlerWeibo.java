package com.ayondo.academy.fragments.social.friend;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.BaseResponseDTO;
import com.ayondo.academy.api.social.InviteFormWeiboDTO;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.network.service.UserServiceWrapper;
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
