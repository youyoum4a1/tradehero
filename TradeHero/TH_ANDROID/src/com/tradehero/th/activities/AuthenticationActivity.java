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
import com.tradehero.th.application.App;
import com.tradehero.th.base.Application;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.authentication.TwitterEmailFragment;
import com.tradehero.th.fragments.authentication.EmailSignUpFragment;
import com.tradehero.th.fragments.authentication.LoginFragment;
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

        setContentView(R.layout.sign_in_up_content);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sign_in_up_content, currentFragment)
                .commit();
    }

    /** map view and the next fragment, which is appears when click on that view */
    private void setupViewFragmentMapping()
    {
        mapViewFragment.put(R.id.authentication_sign_up, SignUpFragment.class);
        mapViewFragment.put(R.id.authentication_sign_in, SignInFragment.class);
        mapViewFragment.put(R.id.txt_email_sign_in, LoginFragment.class);
        mapViewFragment.put(R.id.txt_email_sign_up, EmailSignUpFragment.class);
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
        getSupportActionBar().setCustomView(R.layout.authentication_actionbar);
        return super.onCreateOptionsMenu(menu);
    }

    private static class FragmentFactory
    {
        private static Map<Class<?>, Fragment> instances = new HashMap<>();

        public static Fragment getInstance(Class<?> clss)
        {
            Fragment fragment = instances.get(clss);
            if (fragment == null)
            {
                fragment = Fragment.instantiate(App.context(), clss.getName(), null);
                instances.put(clss, fragment);
            }
            return fragment;
        }
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
            currentFragment = FragmentFactory.getInstance(fragmentClass);
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_right_in, R.anim.slide_left_out,
                            R.anim.slide_left_in, R.anim.slide_right_out)
                    .replace(R.id.sign_in_up_content, currentFragment)
                    .addToBackStack(null)
                    .commit();
            getSupportActionBar().show();
        }

        switch (view.getId())
        {
            case R.id.btn_facebook_signin:
                progressDialog = ProgressDialog.show(
                        AuthenticationActivity.this,
                        Application.getResourceString(R.string.please_wait),
                        Application.getResourceString(R.string.connecting_to_facebook),
                        true);
                FacebookUtils.logIn(this, new LogInCallback()
                {
                    @Override public void done(UserBaseDTO user, THException ex)
                    {
                        progressDialog.hide();
                        ActivityHelper.goRoot(AuthenticationActivity.this);
                    }

                    @Override public boolean onSocialAuthDone(JSONObject json)
                    {
                        progressDialog.setMessage(String.format(getString(R.string.connecting_tradehero), "Facebook"));
                        return true;
                    }
                });
                break;

            case R.id.btn_twitter_signin:
                final boolean isSigningUp = currentFragment != FragmentFactory.getInstance(SignInFragment.class);

                progressDialog = ProgressDialog.show(
                        AuthenticationActivity.this,
                        Application.getResourceString(R.string.please_wait),
                        Application.getResourceString(R.string.connecting_to_twitter),
                        true);
                TwitterUtils.logIn(this, new LogInCallback()
                {
                    @Override public void done(UserBaseDTO user, THException ex)
                    {
                        progressDialog.dismiss();
                        if (user != null)
                        {
                            ActivityHelper.goRoot(AuthenticationActivity.this);
                        }
                        else
                        {
                            THToast.show("Twitter failed: " + ex.getMessage());
                        }
                    }

                    @Override public boolean onSocialAuthDone(JSONObject json)
                    {

                        if (!isSigningUp)
                        {
                            progressDialog.setMessage(String.format(getString(R.string.connecting_tradehero), "Twitter"));
                            return true;
                        }
                        // twitter does not return email for authentication user,
                        // we need to ask user for that
                        progressDialog.hide();
                        progressDialog.setMessage(String.format(getString(R.string.connecting_tradehero), "Twitter"));
                        setTwitterData(json);
                        currentFragment = FragmentFactory.getInstance(TwitterEmailFragment.class);
                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(
                                        R.anim.slide_right_in, R.anim.slide_left_out,
                                        R.anim.slide_left_in, R.anim.slide_right_out)
                                .replace(R.id.sign_in_up_content, currentFragment)
                                .addToBackStack(null)
                                .commit();
                        return false;
                    }
                });
                break;

            case R.id.authentication_twitter_email_button:
                EditText txtTwitterEmail = (EditText) currentFragment.getView().findViewById(R.id.authentication_twitter_email_txt);
                try
                {
                    twitterJson.put("email", txtTwitterEmail.getText());
                    progressDialog.show();
                    THUser.logInAsyncWithJson(twitterJson, new LogInCallback()
                    {
                        @Override public void done(UserBaseDTO user, THException ex)
                        {
                            progressDialog.dismiss();
                            if (user != null)
                            {
                                ActivityHelper.goRoot(AuthenticationActivity.this);
                            }
                        }
                    });
                }
                catch (JSONException e)
                {
                    //nothing for now
                }
                break;

            case R.id.btn_linkedin_signin:
                progressDialog = ProgressDialog.show(AuthenticationActivity.this,
                        Application.getResourceString(R.string.please_wait),
                        Application.getResourceString(R.string.pd_authorizing_linkedIn),
                        true);
                LinkedInUtils.logIn(this, new LogInCallback()
                {
                    @Override public void done(UserBaseDTO user, THException ex)
                    {
                        progressDialog.dismiss();
                        if (user != null)
                        {
                            ActivityHelper.goRoot(AuthenticationActivity.this);
                        }
                        else
                        {
                            THToast.show("LinkedIn failed: " + ex.getMessage());
                        }
                    }

                    @Override public boolean onSocialAuthDone(JSONObject json)
                    {
                        progressDialog.setMessage(String.format(getString(R.string.connecting_tradehero), "LinkedIn"));
                        return true;
                    }
                });
                break;

            case R.id.txt_term_of_service_signin:
                Intent pWebView = new Intent(this, WebViewActivity.class);
                pWebView.putExtra(WebViewActivity.SHOW_URL, Constants.PRIVACY_TERMS_OF_SERVICE);
                startActivity(pWebView);
                break;
        }
    }

    private void setTwitterData(JSONObject json)
    {
        twitterJson = json;
    }
}
