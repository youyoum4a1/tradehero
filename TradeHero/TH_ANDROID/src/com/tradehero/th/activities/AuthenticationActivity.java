package com.tradehero.th.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.tradehero.kit.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.application.App;
import com.tradehero.th.fragments.authentication.InitialSignUpFragment;
import com.tradehero.th.fragments.authentication.WelcomeFragment;
import java.util.HashMap;
import java.util.Map;
import twitter4j.Twitter;

public class AuthenticationActivity extends SherlockFragmentActivity
        implements View.OnClickListener
{
    private static final String TAG = AuthenticationActivity.class.getName();
    private static final String M_FRAGMENT = "M_CURRENT_FRAGMENT";

    private Map<Integer, Class<?>> mapViewFragment = new HashMap<>();
    private Fragment mCurrentFragment;

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
        mapViewFragment.put(R.id.btn_newuser, InitialSignUpFragment.class);
        mapViewFragment.put(R.id.btn_signin, InitialSignUpFragment.class);
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
        } catch (Exception ex)
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

    @Override public void onClick(View view)
    {
        Class<?> fragmentClass = mapViewFragment.get(view.getId());
        if (fragmentClass!= null)
        {
            mCurrentFragment = FragmentFactory.getInstance(fragmentClass);
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out,
                    R.anim.slide_left_in, R.anim.slide_right_out)
                    .replace(R.id.sign_in_up_content, mCurrentFragment)
                    .addToBackStack(null)
                    .commit();
        }

        switch (view.getId())
        {
            default:
                break;
        }
    }
}
