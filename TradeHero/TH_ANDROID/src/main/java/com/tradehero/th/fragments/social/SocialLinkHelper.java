package com.tradehero.th.fragments.social;

import android.app.Activity;
import android.app.ProgressDialog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
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
import javax.inject.Inject;
import retrofit.Callback;

public abstract class SocialLinkHelper
{
    @Inject CurrentUserId currentUserId;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject SocialServiceWrapper socialServiceWrapper;

    Activity context;

    public ProgressDialog progressDialog;

    public SocialLinkHelper(Activity context)
    {
        this.context = context;
    }

    protected abstract SocialNetworkEnum getSocialNetwork();

    protected abstract int getLinkDialogTitle();

    protected abstract int getLinkDialogMessage();

    public void link()
    {
        link(createSocialConnectLogInCallback());
    }

    public void link(LogInCallback callback)
    {
        doLoginAction(context, callback);
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
            progressDialog = progressDialogUtil.show(context,
                    getLinkDialogTitle(),
                    getLinkDialogMessage());

        }

        @Override
        public boolean onSocialAuthDone(JSONCredentials json)
        {
            MiddleCallback middleCallbackConnect = socialServiceWrapper.connect(
                    currentUserId.toUserBaseKey(),
                    UserFormFactory.create(json),
                    createSocialLinkingCallback());

            progressDialog.setMessage(
                    String.format(context.getString(R.string.authentication_connecting_tradehero),
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
}
