package com.tradehero.th.fragments.social.friend;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.social.InviteFormWeiboDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import javax.inject.Inject;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class SocialFriendHandlerWeibo extends SocialFriendHandler
{
    //<editor-fold desc="Constructors">
    @Inject public SocialFriendHandlerWeibo(@NonNull UserServiceWrapper userService)
    {
        super(userService);
    }
    //</editor-fold>

    public MiddleCallback<BaseResponseDTO> inviteWeiboFriends(
            @NonNull String msg,
            @NonNull UserBaseKey userKey,
            @Nullable RequestCallback<BaseResponseDTO> callback)
    {
        return inviteFriends(userKey, new InviteFormWeiboDTO(msg), callback);
    }
}
