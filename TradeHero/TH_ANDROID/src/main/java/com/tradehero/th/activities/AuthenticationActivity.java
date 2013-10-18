package com.tradehero.th.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.auth.AuthenticationMode;
import com.tradehero.th.auth.EmailAuthenticationProvider;
import com.tradehero.th.base.Application;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.authentication.AuthenticationFragment;
import com.tradehero.th.fragments.authentication.EmailSignInFragment;
import com.tradehero.th.fragments.authentication.EmailSignInOrUpFragment;
import com.tradehero.th.fragments.authentication.TwitterEmailFragment;
import com.tradehero.th.fragments.authentication.EmailSignUpFragment;
import com.tradehero.th.fragments.authentication.SignInFragment;
import com.tradehero.th.fragments.authentication.SignUpFragment;
import com.tradehero.th.fragments.authentication.WelcomeFragment;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.utills.Constants;
import com.tradehero.th.utils.FacebookUtils;
import com.tradehero.th.utils.LinkedInUtils;
import com.tradehero.th.utils.TwitterUtils;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit.RetrofitError;

/** Created with IntelliJ IDEA. User: tho Date: 8/14/13 Time: 6:28 PM Copyright (c) TradeHero */
public class AuthenticationActivity extends SherlockFragmentActivity
        implements View.OnClickListener
{
    private static final String TAG = AuthenticationActivity.class.getName();
    private static final String M_FRAGMENT = "M_CURRENT_FRAGMENT";

    private Map<Integer, Class<?>> mapViewFragment = new HashMap<>();
    private Fragment currentFragment;

    private ProgressDialog progressDialog;
    private JSONObject twitterJson;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // check if there is a saved fragment, restore it
        if (savedInstanceState != null)
        {
            currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, M_FRAGMENT);
        }
        else
        {
            currentFragment = FragmentFactory.getInstance(WelcomeFragment.class);
        }

        setupViewFragmentMapping();

        setContentView(R.layout.authentication_layout);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_content, currentFragment)
                .commit();
    }

    /** map view and the next fragment, which is appears when click on that view */
    private void setupViewFragmentMapping()
    {
        mapViewFragment.put(R.id.authentication_by_sign_up_button, SignUpFragment.class);
        mapViewFragment.put(R.id.authentication_by_sign_in_button, SignInFragment.class);
        mapViewFragment.put(R.id.authentication_email_sign_in_link, EmailSignInFragment.class);
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
        }
        catch (Exception ex)
        {
            THLog.e(TAG, "Error saving current Authentication Fragment", ex);
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu)
    {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.topbar_authentication);
        return super.onCreateOptionsMenu(menu);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        FacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }

    @Override public void onClick(View view)
    {
        Class<?> fragmentClass = mapViewFragment.get(view.getId());
        if (fragmentClass != null)
        {
            setCurrentFragmentByClass(fragmentClass);
            if (currentFragment instanceof AuthenticationFragment)
            {
                THUser.setAuthenticationMode(((AuthenticationFragment) currentFragment).getAuthenticationMode());
            }
        }

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

            case R.id.txt_term_of_service_signin:
                Intent pWebView = new Intent(this, WebViewActivity.class);
                pWebView.putExtra(WebViewActivity.SHOW_URL, Constants.PRIVACY_TERMS_OF_SERVICE);
                startActivity(pWebView);
                break;
        }
    }

    private void setCurrentFragmentByClass(Class<?> fragmentClass)
    {
        currentFragment = FragmentFactory.getInstance(fragmentClass);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_right_in, R.anim.slide_left_out,
                        R.anim.slide_left_in, R.anim.slide_right_out)
                .replace(R.id.fragment_content, currentFragment)
                .addToBackStack(null)
                .commit();
    }

    private void authenticateWithEmail()
    {
        if (currentFragment instanceof EmailSignInOrUpFragment)
        {
            progressDialog = ProgressDialog.show(
                    AuthenticationActivity.this,
                    Application.getResourceString(R.string.please_wait),
                    Application.getResourceString(R.string.connecting_tradehero_only),
                    true);
            EmailSignInOrUpFragment castedFragment = (EmailSignInOrUpFragment) currentFragment;
            EmailAuthenticationProvider.setCredentials(castedFragment.getUserFormJSON());
            AuthenticationMode authenticationMode = castedFragment.getAuthenticationMode();
            THUser.setAuthenticationMode(authenticationMode);
            THUser.logInWithAsync(EmailAuthenticationProvider.EMAIL_AUTH_TYPE, createCallbackForEmailSign(authenticationMode));
        }
        else
        {
            throw new IllegalArgumentException("Expected an EmailSignUpFragment or EmailSignInFragment");
        }
    }

    private LogInCallback createCallbackForEmailSign(final AuthenticationMode authenticationMode)
    {
        final boolean isSigningUp = authenticationMode == AuthenticationMode.SignUp;
        return new SocialAuthenticationCallback("Email")
        {
            private boolean signingUp = isSigningUp;

            @Override public boolean isSigningUp()
            {
                return signingUp;
            }

            @Override public boolean onSocialAuthDone(JSONObject json)
            {
                return true;
            }
        };
    }

    //<editor-fold desc="Authenticate with Facebook/Twitter/LinkedIn">
    private void authenticateWithLinkedIn()
    {
        progressDialog = ProgressDialog.show(
                AuthenticationActivity.this,
                Application.getResourceString(R.string.please_wait),
                Application.getResourceString(R.string.connecting_to_linkedin),
                true);
        LinkedInUtils.logIn(this, new SocialAuthenticationCallback("LinkedIn"));
    }

    private void authenticateWithFacebook()
    {
        progressDialog = ProgressDialog.show(
                AuthenticationActivity.this,
                Application.getResourceString(R.string.please_wait),
                Application.getResourceString(R.string.connecting_to_facebook),
                true);
        FacebookUtils.logIn(this, new SocialAuthenticationCallback("Facebook"));
    }

    private void authenticateWithTwitter()
    {
        progressDialog = ProgressDialog.show(
                AuthenticationActivity.this,
                Application.getResourceString(R.string.please_wait),
                Application.getResourceString(R.string.connecting_to_twitter),
                true);
        TwitterUtils.logIn(this, createTwitterAuthenticationCallback());
    }

    private SocialAuthenticationCallback createTwitterAuthenticationCallback ()
    {
        return new SocialAuthenticationCallback("Twitter")
        {
            @Override public boolean isSigningUp()
            {
                return currentFragment != FragmentFactory.getInstance(SignInFragment.class);
            }

            @Override public boolean onSocialAuthDone(JSONObject json)
            {
                if (super.onSocialAuthDone(json))
                {
                    return true;
                }
                // twitter does not return email for authentication user,
                // we need to ask user for that
                setTwitterData(json);
                progressDialog.hide();
                setCurrentFragmentByClass(TwitterEmailFragment.class);
                return false;
            }
        };
    }

    private void complementEmailForTwitterAuthentication()
    {
        EditText txtTwitterEmail = (EditText) currentFragment.getView().findViewById(R.id.authentication_twitter_email_txt);
        try
        {
            twitterJson.put("email", txtTwitterEmail.getText());
            progressDialog.setMessage(String.format(getString(R.string.connecting_tradehero), "Twitter"));
            progressDialog.show();
            THUser.logInAsyncWithJson(twitterJson, createCallbackForTwitterComplementEmail());
        }
        catch (JSONException e)
        {
            //nothing for now
        }
    }

    private LogInCallback createCallbackForTwitterComplementEmail ()
    {
        return new LogInCallback()
        {
            @Override public void done(UserBaseDTO user, THException ex)
            {
                if (user != null)
                {
                    ActivityHelper.goRoot(AuthenticationActivity.this);
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

    private void setTwitterData(JSONObject json)
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

        @Override public void done(UserBaseDTO user, THException ex)
        {
            if (user != null)
            {
                ActivityHelper.goRoot(AuthenticationActivity.this);
            }
            else if (ex.getCause() instanceof RetrofitError && ((RetrofitError) ex.getCause()).getResponse().getStatus() == 403) // Forbidden
            {
                THToast.show(R.string.not_registered);
            }
            else
            {
                THToast.show(ex);
            }

            progressDialog.dismiss();
        }

        @Override public boolean onSocialAuthDone(JSONObject json)
        {
            if (!isSigningUp())
            {
                // HACK
                if (!"Email".equals(providerName))
                {
                    progressDialog.setMessage(String.format(getString(R.string.connecting_tradehero), providerName));
                }
                else
                {
                    progressDialog.setMessage(getString(R.string.connecting_tradehero_only));
                }
                return true;
            }
            return false;
        }

        @Override public void onStart()
        {
            progressDialog.setMessage(getString(R.string.connecting_tradehero_only));
        }

        public boolean isSigningUp()
        {
            return false;
        }

    }

    private static class FragmentFactory
    {
        private static Map<Class<?>, Fragment> instances = new HashMap<>();

        public static Fragment getInstance(Class<?> clss)
        {
            Fragment fragment = instances.get(clss);
            if (fragment == null)
            {
                // TODO should we use Application context to create fragment?
                fragment = Fragment.instantiate(Application.context(), clss.getName(), null);
                instances.put(clss, fragment);
            }
            return fragment;
        }
    }
}
