package com.tradehero.chinabuild.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import com.tradehero.chinabuild.data.sp.THSharePreferenceManager;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.TradeHeroMainActivity;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.auth.AuthenticationMode;
import com.tradehero.th.auth.EmailAuthenticationProvider;
import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.authentication.EmailSignUpFragment;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.user.auth.EmailCredentialsDTO;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import org.json.JSONException;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Register using email.
 */
public class BindEmailSignUpFragment extends EmailSignUpFragment
{
    @Inject ProgressDialogUtil progressDialogUtil;
    private ProgressDialog progressDialog;

    @Override protected void initSetup(View view)
    {
        super.initSetup(view);
        this.emailEditText = (EditText) view.findViewById(R.id.authentication_sign_up_email);
        emailEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {

            }

            @Override public void onTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {
                mIsPhoneNumRegister = false;
                if (charSequence.length() == 11)
                {
                    Pattern p = Pattern.compile("[0-9]*");
                    Matcher m = p.matcher(charSequence);
                    if (m.matches())
                    {
                        mIsPhoneNumRegister = true;
                    }
                }
                if (verifyCodeLayout != null)
                {
                    verifyCodeLayout.setVisibility(mIsPhoneNumRegister ? View.VISIBLE : View.GONE);
                }
            }

            @Override public void afterTextChanged(Editable editable)
            {

            }
        });
        this.passwordEditText = (EditText) view.findViewById(R.id.authentication_sign_up_password);
        verifyCodeLayout = (RelativeLayout) view.findViewById(R.id.login_verify_code_layout);
        verifyCode = (EditText) view.findViewById(R.id.verify_code);
        getVerifyCodeButton = (TextView) view.findViewById(R.id.get_verify_code_button);
        getVerifyCodeButton.setOnClickListener(this);
        this.mDisplayName = (EditText) view.findViewById(R.id.authentication_sign_up_username);
        this.mDisplayName.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {

            }

            @Override public void onTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {
                signButton.setEnabled(charSequence.length() > 3);
            }

            @Override public void afterTextChanged(Editable editable)
            {

            }
        });
        this.mPhoto = (ImageView) view.findViewById(R.id.image_optional);
        mPhoto.setOnClickListener(this);

        this.mAgreeButton = (ImageView) view.findViewById(R.id.authentication_agree);

        this.mAgreeLayout = (LinearLayout) view.findViewById(R.id.authentication_agreement);
        mAgreeLayout.setOnClickListener(this);

        this.mServiceText = (TextView) view.findViewById(R.id.txt_term_of_service);
        mServiceText.setOnClickListener(onClickListener);

        this.signButton = (Button) view.findViewById(R.id.authentication_sign_up_button);
        this.signButton.setOnClickListener(this);
        signButton.setEnabled(false);

        mNextButton = (Button) view.findViewById(R.id.btn_next);
        mNextButton.setOnClickListener(this);
        mSwitcher = (ViewSwitcher) view.findViewById(R.id.switcher);
    }

    @Override public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.authentication_sign_up_button:
                //clear old user info
                THUser.clearCurrentUser();
                handleSignInOrUpButtonClicked(view);
                return;
        }
        super.onClick(view);
    }

    @Override protected void register() throws JSONException
    {
        progressDialog = progressDialogUtil.show(getActivity(), R.string.alert_dialog_please_wait, R.string.authentication_connecting_tradehero_only);
        JSONCredentials createdJson = getUserFormJSON();
        EmailAuthenticationProvider.setCredentials(createdJson);
        AuthenticationMode authenticationMode = getAuthenticationMode();
        THUser.setAuthenticationMode(authenticationMode);
        THUser.logInWithAsync(EmailCredentialsDTO.EMAIL_AUTH_TYPE, createCallbackForEmailSign(authenticationMode));
    }

    private LogInCallback createCallbackForEmailSign(final AuthenticationMode authenticationMode)
    {
        final boolean isSigningUp = authenticationMode == AuthenticationMode.SignUp;
        return new SocialAuthenticationCallback(AnalyticsConstants.LOGIN_USER_ACCOUNT)
        {
            private final boolean signingUp = isSigningUp;

            @Override public boolean isSigningUp()
            {
                return signingUp;
            }

            @Override public boolean onSocialAuthDone(JSONCredentials json)
            {
                return true;
            }
        };
    }

    private class SocialAuthenticationCallback extends LogInCallback
    {
        private final String providerName;

        public SocialAuthenticationCallback(String providerName)
        {
            this.providerName = providerName;
        }

        @Override public void done(UserLoginDTO user, THException ex)
        {
            Throwable cause;
            Response response;
            if (user != null)
            {
                launchDashboard(user);
            }
            else if ((cause = ex.getCause()) != null && cause instanceof RetrofitError &&
                    (response = ((RetrofitError) cause).getResponse()) != null && response.getStatus() == 403) // Forbidden
            {
                THToast.show(R.string.authentication_not_registered);
            }
            else
            {
                THToast.show(ex);
            }

            progressDialog.hide();
        }

        @Override public boolean onSocialAuthDone(JSONCredentials json)
        {
            if (!isSigningUp())
            {
                // HACK
                if (!AnalyticsConstants.LOGIN_USER_ACCOUNT.equals(providerName))
                {
                    if(getActivity() == null){
                        return false;
                    }
                    String thirdPartyName = providerName;
                    if(providerName.equals(AnalyticsConstants.BUTTON_LOGIN_WECHAT)){
                        thirdPartyName = getString(R.string.sign_in_wechat);
                    }
                    if(providerName.equals(AnalyticsConstants.BUTTON_LOGIN_WEIBO)){
                        thirdPartyName = getString(R.string.sign_in_weibo);
                    }
                    progressDialog.setMessage(String.format(getString(R.string.authentication_connecting_tradehero), thirdPartyName));
                }
                else
                {
                    progressDialog.setMessage(getString(R.string.authentication_connecting_tradehero_only));
                }
                progressDialog.show();
                return true;
            }
            return false;
        }

        @Override public void onStart()
        {
            progressDialog.setMessage(getString(R.string.authentication_connecting_tradehero_only));
        }

        public boolean isSigningUp()
        {
            return false;
        }
    }

    private void launchDashboard(UserLoginDTO userLoginDTO)
    {
        THSharePreferenceManager.clearDialogShowedRecord();

        Intent intent = new Intent(getActivity(), TradeHeroMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra(UserLoginDTO.SUGGEST_UPGRADE, userLoginDTO.suggestUpgrade);
        intent.putExtra(UserLoginDTO.SUGGEST_LI_REAUTH, userLoginDTO.suggestLiReauth);
        intent.putExtra(UserLoginDTO.SUGGEST_TW_REAUTH, userLoginDTO.suggestTwReauth);
        intent.putExtra(UserLoginDTO.SUGGEST_FB_REAUTH, userLoginDTO.suggestFbReauth);

        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
    }

    @Override public void onClickHeadLeft()
    {
        if (mSwitcher.getDisplayedChild() == 1)
        {
            mSwitcher.setDisplayedChild(0);
        }
        else
        {
            popCurrentFragment();
        }
    }
}



