package com.tradehero.th.fragments.social.friend;

import android.support.annotation.NonNull;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.social.BatchFollowFormDTO;
import com.tradehero.th.api.social.InviteFormDTO;
import com.tradehero.th.api.social.InviteFormUserDTO;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.service.UserServiceWrapper;
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
        return userServiceWrapper.followBatchFreeRx(new BatchFollowFormDTO(users, null));
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
