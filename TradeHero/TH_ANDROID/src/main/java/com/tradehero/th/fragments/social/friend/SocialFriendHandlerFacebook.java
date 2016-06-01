package com.ayondo.academy.fragments.social.friend;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.facebook.HttpMethod;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.social.facebook.FacebookConstants;
import com.tradehero.common.social.facebook.FacebookRequestOperator;
import com.tradehero.common.social.facebook.FacebookWebDialogOperator;
import com.tradehero.common.utils.CollectionUtils;
import com.ayondo.academy.R;
import com.ayondo.academy.api.BaseResponseDTO;
import com.ayondo.academy.api.auth.AccessTokenForm;
import com.ayondo.academy.api.social.InviteDTO;
import com.ayondo.academy.api.social.InviteFacebookDTO;
import com.ayondo.academy.api.social.InviteFormDTO;
import com.ayondo.academy.api.social.InviteFormUserDTO;
import com.ayondo.academy.api.social.SocialNetworkEnum;
import com.ayondo.academy.api.social.UserFriendsDTOList;
import com.ayondo.academy.api.social.UserFriendsFacebookDTO;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.auth.AuthData;
import com.ayondo.academy.auth.FacebookAuthenticationProvider;
import com.ayondo.academy.auth.operator.FacebookPermissions;
import com.ayondo.academy.models.social.facebook.UserFriendsFacebookUtil;
import com.ayondo.academy.network.service.SocialServiceWrapper;
import com.ayondo.academy.network.service.UserServiceWrapper;
import com.ayondo.academy.persistence.user.UserProfileCacheRx;
import com.ayondo.academy.rx.ReplaceWithFunc1;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
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
    @NonNull private final List<String> permissions;

    //<editor-fold desc="Constructors">
    @Inject public SocialFriendHandlerFacebook(
            @NonNull UserServiceWrapper userServiceWrapper,
            @NonNull CurrentUserId currentUserId,
            @NonNull SocialServiceWrapper socialServiceWrapper,
            @NonNull FacebookAuthenticationProvider facebookAuthenticationProvider,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull Provider<Activity> activityProvider,
            @FacebookPermissions @NonNull List<String> permissions)
    {
        super(userServiceWrapper);
        this.currentUserId = currentUserId;
        this.socialServiceWrapper = socialServiceWrapper;
        this.facebookAuthenticationProvider = facebookAuthenticationProvider;
        this.userProfileCache = userProfileCache;
        this.activityProvider = activityProvider;
        this.permissions = permissions;
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
                        new Func1<InviteDTO, String>()
                        {
                            @Override public String call(InviteDTO invite)
                            {
                                return ((InviteFacebookDTO) invite).fbId;
                            }
                        }),
                null)
                .flatMap(new Func1<Bundle, Observable<? extends String>>()
                {
                    @Override public Observable<? extends String> call(Bundle bundle)
                    {
                        final String requestId = bundle.getString("request");
                        if (requestId != null)
                        {
                            return Observable.just(requestId);
                        }
                        else
                        {
                            return Observable.error(new NullPointerException("RequestId was null in Facebook bundle"));
                        }
                    }
                })
                        // TODO This one should do something useful like trackshare
                .map(new Func1<String, BaseResponseDTO>()
                {
                    @Override public BaseResponseDTO call(String requestId)
                    {
                        return new BaseResponseDTO();
                    }
                });
    }

    @NonNull public Observable<Bundle> createShareRequestObservable(
            @NonNull final List<UserFriendsFacebookDTO> friendsDTOs,
            @SuppressWarnings("UnusedParameters") @Nullable UserFriendsFacebookDTO typeQualifier)
    {
        return createShareRequestObservable(CollectionUtils.map(friendsDTOs, new Func1<UserFriendsFacebookDTO, String>()
        {
            @Override public String call(UserFriendsFacebookDTO friend)
            {
                return friend.fbId;
            }
        }), null);
    }

    @NonNull public Observable<Bundle> createShareRequestObservable(
            @NonNull final List<String> fbIds,
            @SuppressWarnings("UnusedParameters") @Nullable String typeQualifier)
    {
        return createProfileSessionObservable()
                .take(1)
                .flatMap(new Func1<Pair<UserProfileDTO, Session>, Observable<? extends Bundle>>()
                {
                    @Override public Observable<? extends Bundle> call(Pair<UserProfileDTO, Session> pair)
                    {
                        return SocialFriendHandlerFacebook.this.createShareRequestObservable(pair.first, pair.second, fbIds);
                    }
                });
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
                                params)))
                .subscribeOn(AndroidSchedulers.mainThread());
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
        final List<String> newPermissions = new ArrayList<>(permissions);
        newPermissions.add(FacebookConstants.PERMISSION_FRIENDS);
        return Observable.combineLatest(
                userProfileCache.getOne(currentUserId.toUserBaseKey()).map(new PairGetSecond<UserBaseKey, UserProfileDTO>()),
                facebookAuthenticationProvider.createSessionObservable(activityProvider.get(), newPermissions),
                new Func2<UserProfileDTO, Session, Pair<UserProfileDTO, Session>>()
                {
                    @Override public Pair<UserProfileDTO, Session> call(UserProfileDTO t1, Session t2)
                    {
                        return Pair.create(t1, t2);
                    }
                })
                .flatMap(new Func1<Pair<UserProfileDTO, Session>, Observable<? extends Pair<UserProfileDTO, Session>>>()
                {
                    @Override public Observable<? extends Pair<UserProfileDTO, Session>> call(Pair<UserProfileDTO, Session> pair)
                    {
                        if (pair.first.fbLinked)
                        {
                            return socialServiceWrapper.connectRx(
                                    pair.first.getBaseKey(),
                                    new AccessTokenForm(new AuthData(SocialNetworkEnum.FB,
                                            pair.second.getExpirationDate(),
                                            pair.second.getAccessToken())))
                                    .map(new ReplaceWithFunc1<UserProfileDTO, Pair<UserProfileDTO, Session>>(pair));
                        }
                        // Need to link then return
                        return Observable.combineLatest(
                                facebookAuthenticationProvider.createAuthDataObservable(activityProvider.get(), newPermissions)
                                        .observeOn(Schedulers.io())
                                        .flatMap(socialServiceWrapper.connectFunc1(pair.first.getBaseKey())),
                                Observable.just(Session.getActiveSession()),
                                new Func2<UserProfileDTO, Session, Pair<UserProfileDTO, Session>>()
                                {
                                    @Override public Pair<UserProfileDTO, Session> call(UserProfileDTO profile, Session session)
                                    {
                                        return Pair.create(profile, session);
                                    }
                                });
                    }
                });
    }

    @NonNull protected Observable<UserFriendsDTOList> getFetchFacebookInvitableObservable(@NonNull final Bundle parameters)
    {
        return createProfileSessionObservable()
                .take(1)
                .flatMap(new Func1<Pair<UserProfileDTO, Session>, Observable<? extends Response>>()
                {
                    @Override public Observable<? extends Response> call(Pair<UserProfileDTO, Session> pair)
                    {
                        return Observable.create(
                                FacebookRequestOperator
                                        .builder(pair.second, FacebookConstants.API_INVITABLE_FRIENDS)
                                        .setParameters(parameters)
                                        .setHttpMethod(HttpMethod.GET)
                                        .build())
                                .subscribeOn(AndroidSchedulers.mainThread());
                    }
                })
                .map(new Func1<Response, UserFriendsDTOList>()
                {
                    @Override public UserFriendsDTOList call(Response response)
                    {
                        return UserFriendsFacebookUtil.convert(response);
                    }
                });
    }
}
