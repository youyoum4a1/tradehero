package com.tradehero.th.activities;

import android.os.Bundle;
import android.widget.TextView;
import com.facebook.AppEventsLogger;
import com.mobileapptracker.MobileAppTracker;
import com.tapstream.sdk.Api;
import com.tapstream.sdk.Event;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.R;
import com.tradehero.th.auth.operator.FacebookAppId;
import com.tradehero.th.models.time.AppTiming;
import com.tradehero.th.persistence.prefs.AuthHeader;
import com.tradehero.th.persistence.prefs.FirstLaunch;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.VersionUtils;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.AppLaunchEvent;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import dagger.Lazy;
import javax.inject.Inject;

public class SplashActivity extends BaseActivity
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

        setContentView(R.layout.splash_screen);
        TextView appVersion = (TextView) findViewById(R.id.app_version);
        if (appVersion != null)
        {
            appVersion.setText(VersionUtils.getAppVersion(this));
        }
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

    @Override protected boolean requireLogin()
    {
        return false;
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
}
