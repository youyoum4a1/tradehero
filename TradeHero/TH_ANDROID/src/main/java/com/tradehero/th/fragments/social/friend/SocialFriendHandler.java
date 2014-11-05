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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Singleton public class SocialFriendHandler
{
    @NonNull UserServiceWrapper userServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public SocialFriendHandler(@NonNull UserServiceWrapper userServiceWrapper)
    {
        this.userServiceWrapper = userServiceWrapper;
    }
    //</editor-fold>

    public MiddleCallback<UserProfileDTO> followFriends(@NonNull List<UserFriendsDTO> users, @Nullable RequestCallback<UserProfileDTO> callback)
    {
        if (callback != null)
        {
            callback.onRequestStart();
        }
        return userServiceWrapper.followBatchFree(new BatchFollowFormDTO(users, (UserFriendsDTO) null), callback);
    }

    public MiddleCallback<BaseResponseDTO> inviteFriends(
            @NonNull UserBaseKey userKey,
            @NonNull List<UserFriendsDTO> users,
            @Nullable RequestCallback<BaseResponseDTO> callback)
    {
        return inviteFriends(userKey, new InviteFormUserDTO(users), callback);
    }

    public MiddleCallback<BaseResponseDTO> inviteFriends(
            @NonNull UserBaseKey userKey,
            @NonNull InviteFormDTO inviteFormDTO,
            @Nullable RequestCallback<BaseResponseDTO> callback)
    {
        if (callback != null)
        {
            callback.onRequestStart();
        }
        return userServiceWrapper.inviteFriends(userKey, inviteFormDTO, callback);
    }
}
