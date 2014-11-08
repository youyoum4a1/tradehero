package com.tradehero.th.fragments.social.friend;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.facebook.FacebookException;
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
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.auth.FacebookAuthenticationProvider;
import com.tradehero.th.auth.facebook.ObservableWebDialog;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.rx.MakePairFunc2;
import com.tradehero.th.utils.ProgressDialogUtil;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import retrofit.client.Response;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class SocialFriendHandlerFacebook extends SocialFriendHandler
{
    private static final int MAX_FACEBOOK_MESSAGE_LENGTH = 60;
    private static final int MAX_FACEBOOK_FRIENDS_RECEIVERS = 50;

    @NonNull final CurrentUserId currentUserId;
    @NonNull final ProgressDialogUtil dialogUtil;
    @NonNull final SocialServiceWrapper socialServiceWrapper;
    @NonNull private final FacebookAuthenticationProvider facebookAuthenticationProvider;
    @NonNull final UserProfileCache userProfileCache;
    @NonNull private final Provider<Activity> activityProvider;

    private ProgressDialog progressDialog;
    private UserBaseKey userBaseKey;
    private List<UserFriendsDTO> users;
    RequestCallback<BaseResponseDTO> callback;

    //<editor-fold desc="Constructors">
    @Inject public SocialFriendHandlerFacebook(
            @NonNull UserServiceWrapper userServiceWrapper,
            @NonNull CurrentUserId currentUserId,
            @NonNull ProgressDialogUtil dialogUtil,
            @NonNull SocialServiceWrapper socialServiceWrapper,
            @NonNull FacebookAuthenticationProvider facebookAuthenticationProvider,
            @NonNull UserProfileCache userProfileCache,
            @NonNull Provider<Activity> activityProvider)
    {
        super(userServiceWrapper);
        this.currentUserId = currentUserId;
        this.dialogUtil = dialogUtil;
        this.socialServiceWrapper = socialServiceWrapper;
        this.facebookAuthenticationProvider = facebookAuthenticationProvider;
        this.userProfileCache = userProfileCache;
        this.activityProvider = activityProvider;
    }
    //</editor-fold>

    @Override
    public MiddleCallback<BaseResponseDTO> inviteFriends(
            @NonNull UserBaseKey userKey,
            @NonNull List<UserFriendsDTO> users,
            @Nullable RequestCallback<BaseResponseDTO> callback)
    {
        this.userBaseKey = userKey;
        this.users = users;
        this.callback = callback;

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
                .flatMap(new Func1<AuthData, Observable<UserProfileDTO>>()
                {
                    @Override public Observable<UserProfileDTO> call(AuthData authData)
                    {
                        return socialServiceWrapper.connectRx(userKey, new AccessTokenForm(authData));
                    }
                })
                .subscribe(new Observer<UserProfileDTO>()
                {
                    @Override public void onCompleted()
                    {
                    }

                    @Override public void onError(Throwable e)
                    {
                        new SocialLinkingCallback().failure(new THException(e));
                    }

                    @Override public void onNext(UserProfileDTO userProfileDTO)
                    {
                        new SocialLinkingCallback().success(userProfileDTO, (Response) null);
                    }
                });
    }

    private class SocialLinkingCallback extends THCallback<UserProfileDTO>
    {
        @Override protected void success(UserProfileDTO userProfileDTO, THResponse thResponse)
        {
            if (Session.getActiveSession() != null)
            {
                sendRequestDialog(activityProvider.get(), users);
            }
        }

        @Override protected void failure(THException ex)
        {
            // user unlinked current authentication
            THToast.show(ex);
        }

        @Override protected void finish()
        {
            progressDialog.dismiss();
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

        UserProfileDTO userProfileDTO = userProfileCache.get(userBaseKey);
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
                    .setOnCompleteListener(new WebDialog.OnCompleteListener()
                    {
                        @Override public void onComplete(Bundle values, FacebookException error)
                        {
                            if (error != null)
                            {
                                if (error instanceof FacebookOperationCanceledException)
                                {
                                    handleCanceled();
                                }
                                else
                                {
                                    handleError();
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

    private void handleError()
    {
        Timber.d("handleError");
        //THToast.show(R.string.invite_friend_request_error);

        if (callback != null)
        {
            // TODO
            callback.failure(null);
        }
    }

    private void handleSuccess()
    {
        Timber.d("handleSuccess");
        //THToast.show(R.string.invite_friend_request_sent);
        if (callback != null)
        {
            //TODO
            callback.success();
        }
    }

    public Observable<Bundle> createShareRequestObservable(@NonNull final List<UserFriendsFacebookDTO> friendsDTOs)
    {
        return createProfileSessionObservable()
                .flatMap(new Func1<Pair<UserProfileDTO, Session>, Observable<Bundle>>()
                {
                    @Override public Observable<Bundle> call(Pair<UserProfileDTO, Session> pair)
                    {
                        return createShareRequestObservable(pair.first, pair.second, friendsDTOs);
                    }
                });
    }

    public Observable<Pair<UserProfileDTO, Session>> createProfileSessionObservable()
    {
        return Observable.combineLatest(
                userProfileCache.createObservable(currentUserId.toUserBaseKey()),
                facebookAuthenticationProvider.createSessionObservable(activityProvider.get()),
                new MakePairFunc2<UserProfileDTO, Session>())
                .flatMap(new Func1<Pair<UserProfileDTO, Session>, Observable<Pair<UserProfileDTO, Session>>>()
                {
                    @Override public Observable<Pair<UserProfileDTO, Session>> call(final Pair<UserProfileDTO, Session> pair)
                    {
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
                                new MakePairFunc2<UserProfileDTO, Session>());
                    }
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
