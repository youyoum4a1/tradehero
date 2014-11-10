package com.tradehero.th.fragments.social.friend;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.social.InviteFormDTO;
import com.tradehero.th.api.social.InviteFormUserDTO;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.service.UserServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

@Singleton public class SocialFriendHandler
{
    @NonNull UserServiceWrapper userServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public SocialFriendHandler(@NonNull UserServiceWrapper userServiceWrapper)
    {
        this.userServiceWrapper = userServiceWrapper;
    }
    //</editor-fold>

    public Subscription followFriends(@NonNull List<UserFriendsDTO> users, @Nullable RequestObserver<UserProfileDTO> observer)
    {
        if (observer != null)
        {
            observer.onRequestStart();
        }
        return userServiceWrapper.followBatchFreeRx(new BatchFollowFormDTO(users, (UserFriendsDTO) null))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public Subscription inviteFriends(
            @NonNull UserBaseKey userKey,
            @NonNull List<UserFriendsDTO> users,
            @Nullable RequestObserver<BaseResponseDTO> observer)
    {
        return inviteFriends(userKey, new InviteFormUserDTO(users), observer);
    }

    public Subscription inviteFriends(
            @NonNull UserBaseKey userKey,
            @NonNull InviteFormDTO inviteFormDTO,
            @Nullable RequestObserver<BaseResponseDTO> observer)
    {
        if (observer != null)
        {
            observer.onRequestStart();
        }
        return userServiceWrapper.inviteFriendsRx(userKey, inviteFormDTO)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(observer);
    }
}
