package com.tradehero.th.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import com.crashlytics.android.Crashlytics;
import com.facebook.AppEventsLogger;
import com.mobileapptracker.MobileAppTracker;
import com.tapstream.sdk.Api;
import com.tapstream.sdk.Event;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.R;
import com.tradehero.th.UIModule;
import com.tradehero.th.auth.operator.FacebookAppId;
import com.tradehero.th.base.THApp;
import com.tradehero.th.inject.ExInjector;
import com.tradehero.th.inject.Injector;
import com.tradehero.th.models.time.AppTiming;
import com.tradehero.th.persistence.prefs.AuthHeader;
import com.tradehero.th.persistence.prefs.FirstLaunch;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.VersionUtils;
import com.tradehero.th.utils.dagger.AppModule;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.AppLaunchEvent;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import javax.inject.Inject;

public class SplashActivity extends FragmentActivity
{
    @Inject @FacebookAppId String facebookAppId;
    @Inject @FirstLaunch BooleanPreference firstLaunchPreference;

    @Inject Lazy<Api> tapStream;
    @Inject MobileAppTracker mobileAppTracker;
    @Inject Analytics analytics;
    @Inject @AuthHeader String authToken;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        AppTiming.splashCreate = System.currentTimeMillis();
        super.onCreate(savedInstanceState);

        if (Constants.RELEASE)
        {
            Crashlytics.start(this);
        }
        setContentView(R.layout.splash_screen);

        TextView appVersion = (TextView) findViewById(R.id.app_version);
        if (appVersion != null)
        {
            appVersion.setText(VersionUtils.getAppVersion(this));
        }

        Injector newInjector = loadInjector(THApp.get(this));
        newInjector.inject(this);
    }

    protected ExInjector loadInjector(ExInjector injector)
    {
        return injector.plus(new SplashActivityModule());
    }

    @Override protected void onResume()
    {
        super.onResume();

        analytics.openSession();
        analytics.tagScreen(AnalyticsConstants.Loading);

        AppEventsLogger.activateApp(this, facebookAppId);

        tapStream.get().fireEvent(new Event(getString(Constants.TAP_STREAM_TYPE.openResId), false));

        mobileAppTracker.setReferralSources(this);
        mobileAppTracker.measureSession();

        if (!Constants.RELEASE)
        {
            VersionUtils.logScreenMeasurements(this);
        }

        initialisation();
    }

    @Override protected void onPause()
    {
        analytics.closeSession();

        super.onPause();
    }

    protected void initialisation()
    {
        analytics.addEvent(new AppLaunchEvent())
                .addEvent(new SimpleEvent(AnalyticsConstants.LoadingScreen));

        if (firstLaunchPreference.get())
        {
            ActivityHelper.launchGuide(this);
            firstLaunchPreference.set(false);
            finish();
        }
        else if (authToken == null)
        {
            ActivityHelper.launchAuthentication(this);
            finish();
        }
        else
        {
            ActivityHelper.launchDashboard(this);
            finish();
        }
    }

    @Module(
            addsTo = AppModule.class,
            includes = UIModule.class,
            library = true,
            complete = false,
            overrides = true
    )
    public class SplashActivityModule
    {
        @Provides Activity provideActivity()
        {
            return SplashActivity.this;
        }
    }
}
