package com.tradehero.th.fragments.social.friend;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.tradehero.common.utils.CollectionUtils;
import com.tradehero.th.R;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.social.InviteFacebookDTO;
import com.tradehero.th.api.social.InviteFormDTO;
import com.tradehero.th.api.social.InviteFormUserDTO;
import com.tradehero.th.api.social.UserFriendsFacebookDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.auth.FacebookAuthenticationProvider;
import com.tradehero.common.social.facebook.FacebookWebDialogOperator;
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observable;
import rx.schedulers.Schedulers;

public class SocialFriendHandlerFacebook extends SocialFriendHandler
{
    private static final int MAX_FACEBOOK_MESSAGE_LENGTH = 60;
    private static final int MAX_FACEBOOK_FRIENDS_RECEIVERS = 50;

    @NonNull private final CurrentUserId currentUserId;
    @NonNull private final SocialServiceWrapper socialServiceWrapper;
    @NonNull private final FacebookAuthenticationProvider facebookAuthenticationProvider;
    @NonNull private final UserProfileCacheRx userProfileCache;
    @NonNull private final Provider<Activity> activityProvider;

    //<editor-fold desc="Constructors">
    @Inject public SocialFriendHandlerFacebook(
            @NonNull UserServiceWrapper userServiceWrapper,
            @NonNull CurrentUserId currentUserId,
            @NonNull SocialServiceWrapper socialServiceWrapper,
            @NonNull FacebookAuthenticationProvider facebookAuthenticationProvider,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull Provider<Activity> activityProvider)
    {
        super(userServiceWrapper);
        this.currentUserId = currentUserId;
        this.socialServiceWrapper = socialServiceWrapper;
        this.facebookAuthenticationProvider = facebookAuthenticationProvider;
        this.userProfileCache = userProfileCache;
        this.activityProvider = activityProvider;
    }
    //</editor-fold>

    @NonNull @Override public Observable<BaseResponseDTO> inviteFriends(@NonNull UserBaseKey userKey, @NonNull InviteFormDTO inviteFormDTO)
    {
        if (!(inviteFormDTO instanceof InviteFormUserDTO))
        {
            return Observable.error(new IllegalArgumentException("Cannot handle inviteFormDTO of type " + inviteFormDTO.getClass()));
        }

        return createShareRequestObservable(
                CollectionUtils.map(
                        ((InviteFormUserDTO) inviteFormDTO).users,
                        invite -> ((InviteFacebookDTO) invite).fbId),
                null)
                .flatMap(bundle -> {
                    final String requestId = bundle.getString("request");
                    if (requestId != null)
                    {
                        return Observable.just(requestId);
                    }
                    else
                    {
                        return Observable.error(new NullPointerException("RequestId was null in Facebook bundle"));
                    }
                })
                        // TODO This one should do something useful like trackshare
                .map(requestId -> new BaseResponseDTO());
    }

    @NonNull public Observable<Bundle> createShareRequestObservable(
            @NonNull final List<UserFriendsFacebookDTO> friendsDTOs,
            @SuppressWarnings("UnusedParameters") @Nullable UserFriendsFacebookDTO typeQualifier)
    {
        return createShareRequestObservable(CollectionUtils.map(friendsDTOs, friend -> friend.fbId), null);
    }

    @NonNull public Observable<Bundle> createShareRequestObservable(
            @NonNull final List<String> fbIds,
            @SuppressWarnings("UnusedParameters") @Nullable String typeQualifier)
    {
        return createProfileSessionObservable()
                .take(1)
                .flatMap(pair -> createShareRequestObservable(pair.first, pair.second, fbIds));
    }

    @NonNull public Observable<Bundle> createShareRequestObservable(
            @NonNull UserProfileDTO userProfileDTO,
            @NonNull Session session,
            @NonNull List<String> fbIds)
    {
        String messageToFacebookFriends = activityProvider.get().getString(
                R.string.invite_friend_facebook_tradehero_refer_friend_message,
                userProfileDTO.referralCode);
        String concatFriends = concatIds(fbIds);

        //if (messageToFacebookFriends.length() > MAX_FACEBOOK_MESSAGE_LENGTH)
        //{
        //    messageToFacebookFriends = messageToFacebookFriends.substring(0, MAX_FACEBOOK_MESSAGE_LENGTH);
        //}

        Bundle params = new Bundle();
        params.putString("message", messageToFacebookFriends);
        params.putString("to", concatFriends);

        return Observable.create(
                new FacebookWebDialogOperator(
                        new WebDialog.RequestsDialogBuilder(
                                activityProvider.get(),
                                session,
                                params)));
    }

    @NonNull String concatIds(@NonNull List<String> fbIds)
    {
        StringBuilder stringBuilder = new StringBuilder();
        int size = fbIds.size();
        String separator = "";
        for (int i = 0; i < size && i < MAX_FACEBOOK_FRIENDS_RECEIVERS; ++i)
        {
            stringBuilder.append(separator).append(fbIds.get(i));
            separator = ",";
        }
        return stringBuilder.toString();
    }

    @NonNull public Observable<Pair<UserProfileDTO, Session>> createProfileSessionObservable()
    {
        return Observable.combineLatest(
                userProfileCache.get(currentUserId.toUserBaseKey()).map(pair -> pair.second),
                facebookAuthenticationProvider.createSessionObservable(activityProvider.get()),
                Pair::create)
                .flatMap(pair -> {
                    if (pair.first.fbLinked)
                    {
                        return Observable.just(pair);
                    }
                    // Need to link then return
                    return Observable.combineLatest(
                            facebookAuthenticationProvider.createAuthDataObservable(activityProvider.get())
                                    .observeOn(Schedulers.io())
                                    .flatMap(socialServiceWrapper.connectFunc1(pair.first.getBaseKey())),
                            Observable.just(Session.getActiveSession()),
                            Pair::create);
                });
    }
}
