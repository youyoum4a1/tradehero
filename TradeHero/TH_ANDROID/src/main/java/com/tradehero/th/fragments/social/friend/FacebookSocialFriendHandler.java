package com.tradehero.th.fragments.social.friend;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.auth.FacebookAuthenticationProvider;
import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.FacebookUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class FacebookSocialFriendHandler extends SocialFriendHandler
{
    private static final int MAX_FACEBOOK_MESSAGE_LENGTH = 60;
    private static final int MAX_FACEBOOK_FRIENDS_RECEIVERS = 50;

    @NotNull final ProgressDialogUtil dialogUtil;
    @NotNull final Lazy<FacebookUtils> facebookUtils;
    @NotNull final SocialServiceWrapper socialServiceWrapper;
    @NotNull final UserProfileCache userProfileCache;
    @NotNull private final Activity activity;

    private ProgressDialog progressDialog;
    private UserBaseKey userBaseKey;
    private List<UserFriendsDTO> users;
    RequestCallback<Response> callback;

    //<editor-fold desc="Constructors">
    @Inject public FacebookSocialFriendHandler(
            @NotNull Lazy<UserServiceWrapper> userService,
            @NotNull ProgressDialogUtil dialogUtil,
            @NotNull Lazy<FacebookUtils> facebookUtils,
            @NotNull SocialServiceWrapper socialServiceWrapper,
            @NotNull UserProfileCache userProfileCache,
            @NotNull Activity activity)
    {
        super(userService);
        this.dialogUtil = dialogUtil;
        this.facebookUtils = facebookUtils;
        this.socialServiceWrapper = socialServiceWrapper;
        this.userProfileCache = userProfileCache;
        this.activity = activity;
    }
    //</editor-fold>

    public static class FacebookRequestCallback extends RequestCallback
    {

        public FacebookRequestCallback(Context context)
        {
            super(context);
        }

        @Override
        public final void success(Object data, Response response)
        {
            this.success();
        }

        public void success()
        {
        }

        public void failure()
        {
        }

        @Override
        public final void failure(RetrofitError retrofitError)
        {
            this.failure();
        }
    }

    @Override
    public MiddleCallback<Response> inviteFriends(UserBaseKey userKey, List<UserFriendsDTO> users, RequestCallback<Response> callback)
    {
        this.userBaseKey = userKey;
        this.users = users;
        this.callback = callback;

        Session session = getFacebookSession();
        //Session session = Session.getActiveSession();
        if (session == null || session.getAccessToken() == null)
        {
            login(userKey);
        }
        else
        {
            sendRequestDialog(activity, users);
        }
        return null;
    }

    private void login(final UserBaseKey userKey)
    {
        LogInCallback socialNetworkCallback = new LogInCallback()
        {
            @Override public void done(UserLoginDTO user, THException ex)
            {
                Timber.d("login done");
                dialogUtil.dismiss(activity);
            }

            @Override public boolean onSocialAuthDone(JSONCredentials json)
            {
                Timber.d("login onSocialAuthDone");
                //detachMiddleCallbackConnect();
                socialServiceWrapper.connect(
                        userKey,
                        UserFormFactory.create(json),
                        new SocialLinkingCallback());

                progressDialog.setMessage(activity.getString(
                        R.string.authentication_connecting_tradehero,
                        SocialNetworkEnum.FB.getName()));
                return false;
            }

            @Override public void onStart()
            {
                Timber.d("login onStart");
                progressDialog = dialogUtil.show(activity, null, null);
            }
        };
        facebookUtils.get().logIn(activity, socialNetworkCallback);
    }

    private class SocialLinkingCallback extends THCallback<UserProfileDTO>
    {
        @Override protected void success(UserProfileDTO userProfileDTO, THResponse thResponse)
        {
            userProfileCache.put(userBaseKey, userProfileDTO);
            sendRequestDialog(activity, users);
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

    @Inject FacebookAuthenticationProvider facebookAuthenticationProvider;

    private Session getFacebookSession()
    {
        return facebookAuthenticationProvider.getSession();
    }

    private void sendRequestDialog(Activity activity, List<UserFriendsDTO> friendsDTOs)
    {
        Timber.d("sendRequestDialog");
        StringBuilder stringBuilder = new StringBuilder();
        int size = friendsDTOs.size();
        for (int i = 0; i < size && i < MAX_FACEBOOK_FRIENDS_RECEIVERS; ++i)
        {
            stringBuilder.append(friendsDTOs.get(i).fbId).append(',');
        }
        if (stringBuilder.length() > 0)
        {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        Timber.d("list of fbIds: %s", stringBuilder.toString());

        String messageToFacebookFriends = activity.getString(R.string.invite_friend_facebook_tradehero_refer_friend_message);
        if (messageToFacebookFriends.length() > MAX_FACEBOOK_MESSAGE_LENGTH)
        {
            messageToFacebookFriends = messageToFacebookFriends.substring(0, MAX_FACEBOOK_MESSAGE_LENGTH);
        }

        Bundle params = new Bundle();
        params.putString("message", messageToFacebookFriends);
        params.putString("to", stringBuilder.toString());

        Session session = getFacebookSession();
        WebDialog requestsDialog = (
                new WebDialog.RequestsDialogBuilder(activity,
                        session,
                        params))
                .setOnCompleteListener(new WebDialog.OnCompleteListener()
                {

                    @Override
                    public void onComplete(Bundle values,
                            FacebookException error)
                    {
                        if (error != null)
                        {
                            if (error instanceof FacebookOperationCanceledException)
                            {
                                handleCaneled();
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
                                handleCaneled();
                            }
                        }
                    }
                })
                .build();
        requestsDialog.show();
    }

    private void handleCaneled()
    {
        Timber.d("handleCaneled");
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
            // TODO
            callback.success(null, null);
        }
    }
}
