package com.tradehero.th.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import com.tradehero.th.R;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.authentication.SignInOrUpFragment;
import com.tradehero.th.inject.Injector;
import com.tradehero.th.persistence.DTOCacheUtil;
import com.tradehero.th.utils.dagger.AppModule;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import dagger.Module;
import dagger.Provides;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class AuthenticationActivity extends BaseActivity
        implements Injector
{
    @Inject Analytics analytics;
    @Inject DTOCacheUtil dtoCacheUtil;

    private DashboardNavigator navigator;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_layout);

        setupNavigator();
        dtoCacheUtil.clearUserRelatedCaches();
    }

    private void setupNavigator()
    {
        navigator = new DashboardNavigator(this, R.id.fragment_content, SignInOrUpFragment.class, 0);
    }

    @Override protected void onResume()
    {
        super.onResume();
        analytics.openSession();
        analytics.tagScreen(AnalyticsConstants.Login_Register);
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.LoginRegisterScreen));
    }

    @Override protected List<Object> getModules()
    {
        List<Object> superModules = new ArrayList<>(super.getModules());
        superModules.add(new AuthenticationActivityModule());
        return superModules;
    }

    @Override protected void onPause()
    {
        analytics.closeSession();
        super.onPause();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.d("onActivityResult %d, %d, %s", requestCode, resultCode, data);
        Fragment currentFragment = navigator.getCurrentFragment();
        if (currentFragment != null)
        {
            currentFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override protected boolean requireLogin()
    {
        return false;
    }

    @Module(
            addsTo = AppModule.class,
            library = true,
            complete = false,
            overrides = true
    )
    public class AuthenticationActivityModule
    {
        @Provides DashboardNavigator provideDashboardNavigator()
        {
            return navigator;
        }
    }
}
