package com.tradehero.th.fragments.social.friend;

import android.support.annotation.NonNull;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.social.InviteFormWeiboDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.UserServiceWrapper;
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
