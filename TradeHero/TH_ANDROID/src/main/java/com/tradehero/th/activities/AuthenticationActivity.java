package com.tradehero.th.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.tradehero.common.utils.THToast;
import com.tradehero.thm.R;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.auth.AuthenticationMode;
import com.tradehero.th.auth.EmailAuthenticationProvider;
import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.authentication.AuthenticationFragment;
import com.tradehero.th.fragments.authentication.EmailSignInFragment;
import com.tradehero.th.fragments.authentication.EmailSignInOrUpFragment;
import com.tradehero.th.fragments.authentication.EmailSignUpFragment;
import com.tradehero.th.fragments.authentication.SignInFragment;
import com.tradehero.th.fragments.authentication.SignUpFragment;
import com.tradehero.th.fragments.authentication.TwitterEmailFragment;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.user.auth.CredentialsDTOFactory;
import com.tradehero.th.models.user.auth.EmailCredentialsDTO;
import com.tradehero.th.models.user.auth.TwitterCredentialsDTO;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.FacebookUtils;
import com.tradehero.th.utils.LinkedInUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.QQUtils;
import com.tradehero.th.utils.TwitterUtils;
import com.tradehero.th.utils.WeiboUtils;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import com.tradehero.th.utils.metrics.localytics.THLocalyticsSession;
import dagger.Lazy;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.json.JSONException;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class AuthenticationActivity extends SherlockFragmentActivity
        implements View.OnClickListener
{
    private static final String M_FRAGMENT = "M_CURRENT_FRAGMENT";

    private Map<Integer, Class<?>> mapViewFragment = new HashMap<>();
    private Fragment currentFragment;

    private ProgressDialog progressDialog;
    private TwitterCredentialsDTO twitterJson;

    @Inject Lazy<FacebookUtils> facebookUtils;
    @Inject Lazy<TwitterUtils> twitterUtils;
    @Inject Lazy<LinkedInUtils> linkedInUtils;
    @Inject Lazy<WeiboUtils> weiboUtils;
    @Inject Lazy<QQUtils> qqUtils;
    @Inject Lazy<THLocalyticsSession> localyticsSession;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject CurrentActivityHolder currentActivityHolder;
    @Inject CredentialsDTOFactory credentialsDTOFactory;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        DaggerUtils.inject(this);

        currentActivityHolder.setCurrentActivity(this);

        // check if there is a saved fragment, restore it
        if (savedInstanceState != null)
        {
            currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, M_FRAGMENT);
        }

        if (currentFragment == null)
        {
            currentFragment = Fragment.instantiate(this, SignInFragment.class.getName(), null);
            //currentFragment = Fragment.instantiate(this, WelcomeFragment.class.getName(), null);
        }

        setupViewFragmentMapping();

        setContentView(R.layout.authentication_layout);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_content, currentFragment)
                .commit();
    }

    @Override protected void onResume()
    {
        super.onResume();
        List custom_dimensions = new ArrayList();
        custom_dimensions.add(Constants.TAP_STREAM_TYPE.name());
        localyticsSession.get().open(custom_dimensions);
        localyticsSession.get().tagScreen(LocalyticsConstants.Login_Register);
        localyticsSession.get().tagEvent(LocalyticsConstants.LoginRegisterScreen);
    }

    @Override protected void onPause()
    {
        if (progressDialog != null)
        {
            progressDialog.dismiss();
        }
        List custom_dimensions = new ArrayList();
        custom_dimensions.add(Constants.TAP_STREAM_TYPE.name());
        localyticsSession.get().close(custom_dimensions);
        localyticsSession.get().upload();

        super.onPause();
    }

    /** map view and the next fragment, which is appears when click on that view */
    private void setupViewFragmentMapping()
    {
        //two buttons in WelcomeFragment
        mapViewFragment.put(R.id.authentication_by_sign_up_button, SignUpFragment.class);
        mapViewFragment.put(R.id.authentication_by_sign_up_back_button, SignUpFragment.class);
        mapViewFragment.put(R.id.authentication_by_sign_in_button, SignInFragment.class);
        mapViewFragment.put(R.id.authentication_by_sign_in_back_button, SignInFragment.class);
        //button in SignInFragment
        mapViewFragment.put(R.id.authentication_email_sign_in_link, EmailSignInFragment.class);
        //button in SignUpFragment
        mapViewFragment.put(R.id.authentication_email_sign_up_link, EmailSignUpFragment.class);
    }

    @Override protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        try
        {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            {
                getSupportFragmentManager().putFragment(outState, M_FRAGMENT, currentFragment);
            }
        } catch (Exception ex)
        {
            Timber.e("Error saving current Authentication Fragment", ex);
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu)
    {
        //getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setCustomView(R.layout.topbar_authentication);
        getSupportActionBar().hide();
        return super.onCreateOptionsMenu(menu);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.d("onActivityResult %d, %d, %s", requestCode, resultCode, data);
        facebookUtils.get().finishAuthentication(requestCode, resultCode, data);
        weiboUtils.get().authorizeCallBack(requestCode, resultCode, data);
    }

    @Override public void onClick(View view)
    {
        Class<?> fragmentClass = mapViewFragment.get(view.getId());
        if (fragmentClass != null)
        {
            if (view.getId() == R.id.authentication_by_sign_in_back_button
                    || view.getId() == R.id.authentication_by_sign_up_back_button
                    || view.getId() == R.id.authentication_by_sign_in_button)
            {
                InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                setCurrentFragmentByPopBack(fragmentClass);
            }
            else
            {
                setCurrentFragmentByClass(fragmentClass);
            }
            if (currentFragment instanceof AuthenticationFragment)
            {
                THUser.setAuthenticationMode(((AuthenticationFragment) currentFragment).getAuthenticationMode());
                return;
            }
        }
        //TODO maybe shouldn't clear user information here
        THUser.clearCurrentUser();
        switch (view.getId())
        {
            case R.id.authentication_sign_up_button:
            case R.id.btn_login:
                authenticateWithEmail();
                break;

            case R.id.btn_facebook_signin:
                authenticateWithFacebook();
                break;

            case R.id.btn_twitter_signin:
                authenticateWithTwitter();
                break;

            case R.id.authentication_twitter_email_button:
                complementEmailForTwitterAuthentication();
                break;

            case R.id.btn_linkedin_signin:
                authenticateWithLinkedIn();
                break;
            case R.id.btn_weibo_signin:
                authenticateWithWeibo();
                break;

            case R.id.btn_qq_signin:
                authenticateWithQQ();
                break;
            case R.id.txt_term_of_service_signin:
                //TODO WebViewActivity not work, for chromium﹕ [INFO:CONSOLE(17)] "The page at https://www.tradehero.mobi/privacy ran insecure content from http://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400italic,600,700,900.
                //Intent pWebView = new Intent(this, WebViewActivity.class);
                //pWebView.putExtra(WebViewActivity.SHOW_URL, Constants.PRIVACY_TERMS_OF_SERVICE);
                //startActivity(pWebView);
                Uri uri = Uri.parse(Constants.PRIVACY_TERMS_OF_SERVICE);
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
                break;
            case R.id.txt_term_of_service_termsofuse:
                //Intent pWebView2 = new Intent(this, WebViewActivity.class);
                //pWebView2.putExtra(WebViewActivity.SHOW_URL, Constants.PRIVACY_TERMS_OF_USE);
                //startActivity(pWebView2);
                Uri uri2 = Uri.parse(Constants.PRIVACY_TERMS_OF_USE);
                Intent it2 = new Intent(Intent.ACTION_VIEW, uri2);
                startActivity(it2);
                break;
        }
    }

    private void setCurrentFragmentByClass(Class<?> fragmentClass)
    {
        currentFragment = Fragment.instantiate(this, fragmentClass.getName(), null);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_right_in, R.anim.slide_left_out,
                        R.anim.slide_left_in, R.anim.slide_right_out)
                .replace(R.id.fragment_content, currentFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    private void setCurrentFragmentByPopBack(Class<?> fragmentClass)
    {
        getSupportFragmentManager().popBackStack();
    }

    private void authenticateWithEmail()
    {
        if (currentFragment instanceof EmailSignInOrUpFragment)
        {
            progressDialog = progressDialogUtil.show(this, R.string.alert_dialog_please_wait, R.string.authentication_connecting_tradehero_only);
            EmailSignInOrUpFragment castedFragment = (EmailSignInOrUpFragment) currentFragment;
            JSONCredentials createdJson = castedFragment.getUserFormJSON();
            EmailAuthenticationProvider.setCredentials(createdJson);
            AuthenticationMode authenticationMode = castedFragment.getAuthenticationMode();
            THUser.setAuthenticationMode(authenticationMode);
            THUser.logInWithAsync(EmailCredentialsDTO.EMAIL_AUTH_TYPE, createCallbackForEmailSign(authenticationMode));
        }
        else
        {
            throw new IllegalArgumentException("Expected an EmailSignUpFragment or EmailSignInFragment");
        }
    }

    private LogInCallback createCallbackForEmailSign(final AuthenticationMode authenticationMode)
    {
        final boolean isSigningUp = authenticationMode == AuthenticationMode.SignUp;
        return new SocialAuthenticationCallback(LocalyticsConstants.Email)
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

    //<editor-fold desc="Authenticate with Facebook/Twitter/LinkedIn">

    /**
     * Chinese
     */
    public void authenticateWithWeibo()
    {
        localyticsSession.get().tagEventMethod(LocalyticsConstants.SignUp_Tap, LocalyticsConstants.WeiBo);
        progressDialog = progressDialogUtil.show(this, R.string.alert_dialog_please_wait, R.string.authentication_connecting_to_weibo);
        weiboUtils.get().logIn(this, new SocialAuthenticationCallback(LocalyticsConstants.WeiBo));
    }

    public void authenticateWithQQ()
    {
        localyticsSession.get().tagEventMethod(LocalyticsConstants.SignUp_Tap, LocalyticsConstants.QQ);
        progressDialog = progressDialogUtil.show(this, R.string.alert_dialog_please_wait, R.string.authentication_connecting_to_qq);
        qqUtils.get().logIn(this, new SocialAuthenticationCallback(LocalyticsConstants.QQ));
    }

    public void authenticateWithLinkedIn()
    {
        localyticsSession.get().tagEventMethod(LocalyticsConstants.SignUp_Tap, LocalyticsConstants.Linkedin);
        progressDialog = progressDialogUtil.show(this, R.string.alert_dialog_please_wait, R.string.authentication_connecting_to_linkedin);
        linkedInUtils.get().logIn(this, new SocialAuthenticationCallback(LocalyticsConstants.Linkedin));
    }

    public void authenticateWithFacebook()
    {
        localyticsSession.get().tagEventMethod(LocalyticsConstants.SignUp_Tap,
                LocalyticsConstants.Facebook);
        progressDialog = progressDialogUtil.show(this, R.string.alert_dialog_please_wait, R.string.authentication_connecting_to_facebook);
        facebookUtils.get().logIn(this, new SocialAuthenticationCallback(LocalyticsConstants.Facebook));
    }

    public void authenticateWithTwitter()
    {
        localyticsSession.get().tagEventMethod(LocalyticsConstants.SignUp_Tap, LocalyticsConstants.Twitter);
        progressDialog = progressDialogUtil.show(this, R.string.alert_dialog_please_wait, R.string.authentication_twitter_connecting);
        twitterUtils.get().logIn(this, createTwitterAuthenticationCallback());
    }

    private SocialAuthenticationCallback createTwitterAuthenticationCallback()
    {
        return new SocialAuthenticationCallback(LocalyticsConstants.Twitter)
        {
            @Override public boolean isSigningUp()
            {
                return !(currentFragment instanceof SignInFragment);
            }

            @Override public boolean onSocialAuthDone(JSONCredentials json)
            {
                if (super.onSocialAuthDone(json))
                {
                    return true;
                }
                // twitter does not return email for authentication user,
                // we need to ask user for that
                try
                {
                    setTwitterData((TwitterCredentialsDTO) credentialsDTOFactory.create(json));
                } catch (JSONException | ParseException e)
                {
                    Timber.e(e, "Failed to create twitter credentials with %s", json);
                }
                progressDialog.hide();
                setCurrentFragmentByClass(TwitterEmailFragment.class);
                return false;
            }
        };
    }

    private void complementEmailForTwitterAuthentication()
    {
        EditText txtTwitterEmail = (EditText) currentFragment.getView().findViewById(R.id.authentication_twitter_email_txt);
        twitterJson.email = txtTwitterEmail.getText().toString();
        progressDialog.setMessage(String.format(getString(R.string.authentication_connecting_tradehero), "Twitter"));
        progressDialog.show();
        THUser.logInAsyncWithJson(twitterJson, createCallbackForTwitterComplementEmail());
    }

    private LogInCallback createCallbackForTwitterComplementEmail()
    {
        return new LogInCallback()
        {
            @Override public void done(UserLoginDTO user, THException ex)
            {
                if (user != null)
                {
                    localyticsSession.get().tagEventMethod(LocalyticsConstants.SignUp_Success, LocalyticsConstants.Twitter);
                    launchDashboard(user);
                    finish();
                }
                else
                {
                    THToast.show(ex);
                }
                progressDialog.dismiss();
            }

            @Override public void onStart()
            {
                // do nothing for now
            }
        };
    }

    private void launchDashboard(UserLoginDTO userLoginDTO)
    {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra(UserLoginDTO.SUGGEST_UPGRADE, userLoginDTO.suggestUpgrade);
        intent.putExtra(UserLoginDTO.SUGGEST_LI_REAUTH, userLoginDTO.suggestLiReauth);
        intent.putExtra(UserLoginDTO.SUGGEST_TW_REAUTH, userLoginDTO.suggestTwReauth);
        intent.putExtra(UserLoginDTO.SUGGEST_FB_REAUTH, userLoginDTO.suggestFbReauth);

        startActivity(intent);
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
        finish();
    }

    private void setTwitterData(TwitterCredentialsDTO json)
    {
        twitterJson = json;
    }
    //</editor-fold>

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
                localyticsSession.get().tagEventMethod(LocalyticsConstants.SignUp_Success, providerName);
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
                if (!LocalyticsConstants.Email.equals(providerName))
                {
                    progressDialog.setMessage(String.format(getString(R.string.authentication_connecting_tradehero), providerName));
                }
                else
                {
                    progressDialog.setMessage(getString(R.string.authentication_connecting_tradehero_only));
                }
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
}
