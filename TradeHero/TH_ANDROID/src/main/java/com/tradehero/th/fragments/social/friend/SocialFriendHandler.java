package com.tradehero.th.fragments.social.friend;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.social.InviteFormDTO;
import com.tradehero.th.api.social.InviteFormUserDTO;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class SocialFriendHandler
{
    @NotNull UserServiceWrapper userServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public SocialFriendHandler(@NotNull UserServiceWrapper userServiceWrapper)
    {
        this.userServiceWrapper = userServiceWrapper;
    }
    //</editor-fold>

    public MiddleCallback<UserProfileDTO> followFriends(@NotNull List<UserFriendsDTO> users, @Nullable RequestCallback<UserProfileDTO> callback)
    {
        if (callback != null)
        {
            callback.onRequestStart();
        }
        return userServiceWrapper.followBatchFree(new BatchFollowFormDTO(users, (UserFriendsDTO) null), callback);
    }

    public MiddleCallback<BaseResponseDTO> inviteFriends(
            @NotNull UserBaseKey userKey,
            @NotNull List<UserFriendsDTO> users,
            @Nullable RequestCallback<BaseResponseDTO> callback)
    {
        return inviteFriends(userKey, new InviteFormUserDTO(users), callback);
    }

    public MiddleCallback<BaseResponseDTO> inviteFriends(
            @NotNull UserBaseKey userKey,
            @NotNull InviteFormDTO inviteFormDTO,
            @Nullable RequestCallback<BaseResponseDTO> callback)
    {
        if (callback != null)
        {
            callback.onRequestStart();
        }
        return userServiceWrapper.inviteFriends(userKey, inviteFormDTO, callback);
    }
}
