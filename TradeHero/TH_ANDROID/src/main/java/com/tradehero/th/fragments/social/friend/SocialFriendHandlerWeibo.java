package com.tradehero.th.fragments.social.friend;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.social.InviteFormWeiboDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SocialFriendHandlerWeibo extends SocialFriendHandler
{
    //<editor-fold desc="Constructors">
    @Inject public SocialFriendHandlerWeibo(@NotNull UserServiceWrapper userService)
    {
        super(userService);
    }
    //</editor-fold>

    public MiddleCallback<BaseResponseDTO> inviteWeiboFriends(
            @NotNull String msg,
            @NotNull UserBaseKey userKey,
            @Nullable RequestCallback<BaseResponseDTO> callback)
    {
        return inviteFriends(userKey, new InviteFormWeiboDTO(msg), callback);
    }
}
