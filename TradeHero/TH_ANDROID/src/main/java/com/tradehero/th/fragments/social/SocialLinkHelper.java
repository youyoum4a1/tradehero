package com.tradehero.th.fragments.social;

import android.app.Activity;
import android.app.ProgressDialog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.utils.ProgressDialogUtil;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public abstract class SocialLinkHelper
{
    @NotNull protected final CurrentUserId currentUserId;
    @NotNull protected final ProgressDialogUtil progressDialogUtil;
    @NotNull protected final SocialServiceWrapper socialServiceWrapper;
    @NotNull protected final CurrentActivityHolder currentActivityHolder;

    protected Callback<UserProfileDTO> socialLinkingCallback;
    protected MiddleCallback<UserProfileDTO> middleCallbackSocialLinking;

    public ProgressDialog progressDialog;

    protected SocialLinkHelper(
            @NotNull CurrentUserId currentUserId,
            @NotNull ProgressDialogUtil progressDialogUtil,
            @NotNull SocialServiceWrapper socialServiceWrapper,
            @NotNull CurrentActivityHolder currentActivityHolder)
    {
        this.currentUserId = currentUserId;
        this.progressDialogUtil = progressDialogUtil;
        this.socialServiceWrapper = socialServiceWrapper;
        this.currentActivityHolder = currentActivityHolder;
    }

    public void onDestroyView()
    {
        detachMiddleCallbackSocialLinking();
        setSocialLinkingCallback(null);
    }

    public void setSocialLinkingCallback(Callback<UserProfileDTO> socialLinkingCallback)
    {
        this.socialLinkingCallback = socialLinkingCallback;
    }

    protected void detachMiddleCallbackSocialLinking()
    {
        if (middleCallbackSocialLinking != null)
        {
            middleCallbackSocialLinking.setPrimaryCallback(null);
        }
        middleCallbackSocialLinking = null;
    }

    protected abstract SocialNetworkEnum getSocialNetwork();

    protected abstract int getLinkDialogTitle();

    protected abstract int getLinkDialogMessage();

    public void link()
    {
        link(null);
    }

    public void link(Callback<UserProfileDTO> socialLinkingCallback)
    {
        this.socialLinkingCallback = socialLinkingCallback;
        doLoginAction(currentActivityHolder.getCurrentActivity(), createSocialConnectLogInCallback());
    }

    protected abstract void doLoginAction(Activity context, LogInCallback logInCallback);

    protected LogInCallback createSocialConnectLogInCallback()
    {
        return new SocialConnectLoginCallback();
    }

    protected class SocialConnectLoginCallback extends LogInCallback
    {
        @Override
        public void done(UserLoginDTO user, THException ex)
        {
            // when user cancel the process
            progressDialog.hide();
        }

        @Override
        public void onStart()
        {
            progressDialog = progressDialogUtil.show(
                    currentActivityHolder.getCurrentContext(),
                    getLinkDialogTitle(),
                    getLinkDialogMessage());
        }

        @Override
        public boolean onSocialAuthDone(JSONCredentials json)
        {
            detachMiddleCallbackSocialLinking();
            middleCallbackSocialLinking = socialServiceWrapper.connect(
                    currentUserId.toUserBaseKey(),
                    UserFormFactory.create(json),
                    createSocialLinkingCallback());

            progressDialog.setMessage(
                    String.format(currentActivityHolder.getCurrentContext().getString(R.string.authentication_connecting_tradehero),
                            getSocialNetwork().getName()));
            return false;
        }
    }

    protected Callback<UserProfileDTO> createSocialLinkingCallback()
    {
        return new SocialLinkingCallback();
    }

    protected class SocialLinkingCallback extends THCallback<UserProfileDTO>
    {
        @Override public void success(UserProfileDTO userProfileDTO, Response response)
        {
            super.success(userProfileDTO, response);
            notifyLinkingComplete(userProfileDTO, response);
        }

        @Override public void failure(RetrofitError error)
        {
            super.failure(error);
            notifyLinkingFailure(error);
        }

        @Override
        protected void success(UserProfileDTO userProfileDTO, THResponse thResponse)
        {
        }

        @Override
        protected void failure(THException ex)
        {
            // user unlinked current authentication
            THToast.show(ex);
        }

        @Override
        protected void finish()
        {
            progressDialog.hide();
            //updateSocialConnectStatus();
        }
    }

    protected void notifyLinkingComplete(UserProfileDTO userProfileDTO, Response response)
    {
        Callback<UserProfileDTO> callbackCopy = socialLinkingCallback;
        if (callbackCopy != null)
        {
            callbackCopy.success(userProfileDTO, response);
        }
    }

    protected void notifyLinkingFailure(RetrofitError retrofitError)
    {
        Callback<UserProfileDTO> callbackCopy = socialLinkingCallback;
        if (callbackCopy != null)
        {
            callbackCopy.failure(retrofitError);
        }
    }
}
