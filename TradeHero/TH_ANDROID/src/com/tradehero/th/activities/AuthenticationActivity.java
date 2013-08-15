package com.tradehero.th.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ProgressBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.tradehero.kit.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.application.App;
import com.tradehero.th.base.Application;
import com.tradehero.th.fragments.authentication.LoginFragment;
import com.tradehero.th.fragments.authentication.SignInFragment;
import com.tradehero.th.fragments.authentication.SignUpFragment;
import com.tradehero.th.fragments.authentication.WelcomeFragment;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.utils.FacebookUtils;
import com.tradehero.th.utils.LinkedInUtils;
import com.tradehero.th.utils.TwitterUtils;
import com.tradehero.th.webbrowser.WebViewActivity;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationActivity extends SherlockFragmentActivity
        implements View.OnClickListener
{
    private static final String TAG = AuthenticationActivity.class.getName();
    private static final String M_FRAGMENT = "M_CURRENT_FRAGMENT";

    private Map<Integer, Class<?>> mapViewFragment = new HashMap<>();
    private Fragment mCurrentFragment;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // check if there is a saved fragment, restore it
        if (savedInstanceState != null)
        {
            mCurrentFragment = getSupportFragmentManager()
                    .getFragment(savedInstanceState, M_FRAGMENT);
        }
        else
        {
            mCurrentFragment = FragmentFactory.getInstance(WelcomeFragment.class);
        }

        setupViewFragmentMapping();
        setContentView(R.layout.sign_in_up_content);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sign_in_up_content, mCurrentFragment)
                .commit();
    }

    private void setupViewFragmentMapping()
    {
        mapViewFragment.put(R.id.btn_signup, SignUpFragment.class);
        mapViewFragment.put(R.id.btn_signin, SignInFragment.class);
        mapViewFragment.put(R.id.txt_email_signin, LoginFragment.class);
        mapViewFragment.put(R.id.txt_term_of_service_signin, WebViewActivity.class);
    }

    @Override protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        try
        {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            {
                getSupportFragmentManager().putFragment(outState, M_FRAGMENT, mCurrentFragment);
            }
        }
        catch (Exception ex)
        {
            THLog.e(TAG, "Error saving current Authentication Fragment", ex);
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
            mCurrentFragment = FragmentFactory.getInstance(fragmentClass);
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_right_in, R.anim.slide_left_out,
                            R.anim.slide_left_in, R.anim.slide_right_out)
                    .replace(R.id.sign_in_up_content, mCurrentFragment)
                    .addToBackStack(null)
                    .commit();
        }

        switch (view.getId())
        {
            case R.id.btn_facebook_signin:
                FacebookUtils.logIn(this, new LogInCallback()
                {
                    @Override public void done(UserBaseDTO user, THException ex)
                    {
                        progressDialog.hide();
                        ActivityHelper.goRoot(AuthenticationActivity.this);
                    }

                    @Override public void onStart()
                    {
                        progressDialog = ProgressDialog.show(AuthenticationActivity.this,
                                Application.context().getResourceString(R.string.fh_please_wait),
                                Application.getResourceString(R.string.fh_connecting_to_facebook), true);
                    }
                });
                break;
            case R.id.btn_twitter_signin:
                TwitterUtils.logIn(this, new LogInCallback()
                {

                    @Override public void done(UserBaseDTO user, THException ex)
                    {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    @Override public void onStart()
                    {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }
                });
                break;
            case R.id.btn_linkedin_signin:
                LinkedInUtils.logIn(this, new LogInCallback()
                {

                    @Override public void done(UserBaseDTO user, THException ex)
                    {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    @Override public void onStart()
                    {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }
                });
                break;
        }
    }
}
