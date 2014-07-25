package com.tradehero.th.fragments.social.friend;

import com.tradehero.th.api.social.InviteFormDTO;
import com.tradehero.th.api.social.InviteFormWeiboDTO;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import retrofit.client.Response;

public class SocialFriendHandlerWeibo extends SocialFriendHandler
{
    //<editor-fold desc="Constructors">
    @Inject public SocialFriendHandlerWeibo(@NotNull Lazy<UserServiceWrapper> userService)
    {
        super(userService);
    }
    //</editor-fold>

    public MiddleCallback<Response> inviteWeiboFriends(String msg, @NotNull UserBaseKey userKey, List<UserFriendsDTO> users, RequestCallback<Response> callback)
    {
        InviteFormDTO inviteFormDTO = new InviteFormWeiboDTO(msg);
        //inviteFormDTO.addAll(users);
        return inviteFriends(userKey, inviteFormDTO, callback);
    }
}
