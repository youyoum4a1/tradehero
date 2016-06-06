package com.androidth.general.fragments.social.friend;

import android.support.annotation.NonNull;
import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.social.BatchFollowFormDTO;
import com.androidth.general.api.social.InviteFormDTO;
import com.androidth.general.api.social.InviteFormUserDTO;
import com.androidth.general.api.social.UserFriendsDTO;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.network.service.UserServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton public class SocialFriendHandler
{
    @NonNull UserServiceWrapper userServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public SocialFriendHandler(@NonNull UserServiceWrapper userServiceWrapper)
    {
        this.userServiceWrapper = userServiceWrapper;
    }
    //</editor-fold>

    @NonNull public Observable<UserProfileDTO> followFriends(@NonNull List<UserFriendsDTO> users)
    {
        return userServiceWrapper.followBatchFreeRx(new BatchFollowFormDTO(users, (UserFriendsDTO) null));
    }

    @NonNull public Observable<BaseResponseDTO> inviteFriends(
            @NonNull UserBaseKey userKey,
            @NonNull List<UserFriendsDTO> users)
    {
        return inviteFriends(userKey, new InviteFormUserDTO(users));
    }

    @NonNull public Observable<BaseResponseDTO> inviteFriends(
            @NonNull UserBaseKey userKey,
            @NonNull InviteFormDTO inviteFormDTO)
    {
        return userServiceWrapper.inviteFriendsRx(userKey, inviteFormDTO);
    }
}
