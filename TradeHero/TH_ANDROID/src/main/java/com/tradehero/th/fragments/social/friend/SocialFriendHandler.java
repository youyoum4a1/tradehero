package com.ayondo.academy.fragments.social.friend;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.BaseResponseDTO;
import com.ayondo.academy.api.social.BatchFollowFormDTO;
import com.ayondo.academy.api.social.InviteFormDTO;
import com.ayondo.academy.api.social.InviteFormUserDTO;
import com.ayondo.academy.api.social.UserFriendsDTO;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.network.service.UserServiceWrapper;
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
