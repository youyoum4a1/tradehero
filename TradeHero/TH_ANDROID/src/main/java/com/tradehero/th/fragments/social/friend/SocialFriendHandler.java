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
import rx.Observable;
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

    @NonNull public Observable<UserProfileDTO> followFriends(@NonNull List<UserFriendsDTO> users)
    {
        return userServiceWrapper.followBatchFreeRx(new BatchFollowFormDTO(users, (UserFriendsDTO) null));
    }

    @NonNull public Subscription followFriends(@NonNull List<UserFriendsDTO> users, @Nullable RequestObserver<UserProfileDTO> observer)
    {
        if (observer != null)
        {
            observer.onRequestStart();
        }
        return followFriends(users)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
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

    @NonNull public Subscription inviteFriends(
            @NonNull UserBaseKey userKey,
            @NonNull List<UserFriendsDTO> users,
            @Nullable RequestObserver<BaseResponseDTO> observer)
    {
        return inviteFriends(userKey, new InviteFormUserDTO(users), observer);
    }

    @NonNull public Subscription inviteFriends(
            @NonNull UserBaseKey userKey,
            @NonNull InviteFormDTO inviteFormDTO,
            @Nullable RequestObserver<BaseResponseDTO> observer)
    {
        if (observer != null)
        {
            observer.onRequestStart();
        }
        return inviteFriends(userKey, inviteFormDTO)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(observer);
    }
}
