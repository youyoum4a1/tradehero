package com.tradehero.th.fragments.social.friend;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.auth.AccessTokenForm;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.social.UserFriendsFacebookDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.auth.FacebookAuthenticationProvider;
import com.tradehero.th.auth.facebook.ObservableWebDialog;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.MakePairFunc2;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class SocialFriendHandlerFacebook extends SocialFriendHandler
{
    private static final int MAX_FACEBOOK_MESSAGE_LENGTH = 60;
    private static final int MAX_FACEBOOK_FRIENDS_RECEIVERS = 50;

    @NonNull final CurrentUserId currentUserId;
    @NonNull final SocialServiceWrapper socialServiceWrapper;
    @NonNull private final FacebookAuthenticationProvider facebookAuthenticationProvider;
    @NonNull final UserProfileCacheRx userProfileCache;
    @NonNull private final Provider<Activity> activityProvider;

    private UserBaseKey userBaseKey;
    private List<UserFriendsDTO> users;
    RequestObserver<BaseResponseDTO> observer;

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

    @Override
    public Subscription inviteFriends(
            @NonNull UserBaseKey userKey,
            @NonNull List<UserFriendsDTO> users,
            @Nullable RequestObserver<BaseResponseDTO> observer)
    {
        this.userBaseKey = userKey;
        this.users = users;
        this.observer = observer;

        //Session session = getFacebookSession();
        Session session = Session.getActiveSession();
        if (session == null || session.getAccessToken() == null)
        {
            login(userKey);
        }
        else
        {
            sendRequestDialog(activityProvider.get(), users);
        }
        return null;
    }

    private void login(final UserBaseKey userKey)
    {
        // TODO/refactor
        facebookAuthenticationProvider.logIn(activityProvider.get())
                .map(AccessTokenForm::new)
                .flatMap(accessTokenForm -> socialServiceWrapper.connectRx(userKey, accessTokenForm))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SocialLinkingObserver());
    }

    private class SocialLinkingObserver implements Observer<UserProfileDTO>
    {
        @Override public void onNext(UserProfileDTO userProfileDTO)
        {
            if (Session.getActiveSession() != null)
            {
                sendRequestDialog(activityProvider.get(), users);
            }
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable ex)
        {
            // user unlinked current authentication
            THToast.show(new THException(ex));
        }
    }

    private void sendRequestDialog(@NonNull Activity activity, @NonNull List<UserFriendsDTO> friendsDTOs)
    {
        Timber.d("sendRequestDialog");
        StringBuilder stringBuilder = new StringBuilder();
        int size = friendsDTOs.size();
        String separator = "";
        for (int i = 0; i < size && i < MAX_FACEBOOK_FRIENDS_RECEIVERS; ++i)
        {
            stringBuilder.append(separator)
                    .append(((UserFriendsFacebookDTO) friendsDTOs.get(i)).fbId);
            separator = ",";
        }
        Timber.d("list of fbIds: %s", stringBuilder.toString());

        UserProfileDTO userProfileDTO = userProfileCache.getValue(userBaseKey);
        if (userProfileDTO != null)
        {
            String messageToFacebookFriends =
                    activity.getString(R.string.invite_friend_facebook_tradehero_refer_friend_message, userProfileDTO.referralCode);
            //if (messageToFacebookFriends.length() > MAX_FACEBOOK_MESSAGE_LENGTH)
            //{
            //    messageToFacebookFriends = messageToFacebookFriends.substring(0, MAX_FACEBOOK_MESSAGE_LENGTH);
            //}

            Bundle params = new Bundle();
            params.putString("message", messageToFacebookFriends);
            params.putString("to", stringBuilder.toString());

            //Session session = getFacebookSession();
            Session session = Session.getActiveSession();
            WebDialog requestsDialog = (
                    new WebDialog.RequestsDialogBuilder(activity,
                            session,
                            params))
                    .setOnCompleteListener((values, error) -> {
                        if (error != null)
                        {
                            if (error instanceof FacebookOperationCanceledException)
                            {
                                handleCanceled();
                            }
                            else
                            {
                                handleError(error);
                            }
                        }
                        else
                        {
                            final String requestId = values.getString("request");
                            if (requestId != null)
                            {
                                handleSuccess();
                            }
                            else
                            {
                                handleCanceled();
                            }
                        }
                    })
                    .build();
            requestsDialog.show();
        }
    }

    private void handleCanceled()
    {
        Timber.d("handleCanceled");
        THToast.show(R.string.invite_friend_request_canceled);
    }

    private void handleError(@NonNull Throwable error)
    {
        Timber.d("handleError");
        //THToast.show(R.string.invite_friend_request_error);

        if (observer != null)
        {
            // TODO
            observer.onError(error);
        }
    }

    private void handleSuccess()
    {
        Timber.d("handleSuccess");
        //THToast.show(R.string.invite_friend_request_sent);
        if (observer != null)
        {
            //TODO
            observer.success();
        }
    }

    public Observable<Bundle> createShareRequestObservable(@NonNull final List<UserFriendsFacebookDTO> friendsDTOs)
    {
        return createProfileSessionObservable()
                .flatMap(pair -> createShareRequestObservable(pair.first, pair.second, friendsDTOs));
    }

    public Observable<Pair<UserProfileDTO, Session>> createProfileSessionObservable()
    {
        return Observable.combineLatest(
                userProfileCache.get(currentUserId.toUserBaseKey()).map(pair -> pair.second),
                facebookAuthenticationProvider.createSessionObservable(activityProvider.get()),
                new MakePairFunc2<>())
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
                            new MakePairFunc2<>());
                });
    }

    public Observable<Bundle> createShareRequestObservable(
            @NonNull UserProfileDTO userProfileDTO,
            @NonNull Session session,
            @NonNull List<UserFriendsFacebookDTO> friendsDTOs)
    {
        Timber.d("sendRequestDialog");
        StringBuilder stringBuilder = new StringBuilder();
        int size = friendsDTOs.size();
        String separator = "";
        for (int i = 0; i < size && i < MAX_FACEBOOK_FRIENDS_RECEIVERS; ++i)
        {
            stringBuilder.append(separator)
                    .append(friendsDTOs.get(i).fbId);
            separator = ",";
        }
        Timber.d("list of fbIds: %s", stringBuilder.toString());

        String messageToFacebookFriends =
                activityProvider.get().getString(R.string.invite_friend_facebook_tradehero_refer_friend_message, userProfileDTO.referralCode);

        //if (messageToFacebookFriends.length() > MAX_FACEBOOK_MESSAGE_LENGTH)
        //{
        //    messageToFacebookFriends = messageToFacebookFriends.substring(0, MAX_FACEBOOK_MESSAGE_LENGTH);
        //}

        Bundle params = new Bundle();
        params.putString("message", messageToFacebookFriends);
        params.putString("to", stringBuilder.toString());

        WebDialog requestsDialog = (
                new WebDialog.RequestsDialogBuilder(
                        activityProvider.get(),
                        session,
                        params))
                .build();
        requestsDialog.show();
        return ObservableWebDialog.create(requestsDialog);
    }
}
