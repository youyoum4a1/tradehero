package com.ayondo.academy.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.widget.TextView;

import com.facebook.AppEventsLogger;
import com.mobileapptracker.MobileAppTracker;
import com.tapstream.sdk.Api;
import com.tapstream.sdk.Event;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.ayondo.academy.R;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.models.time.AppTiming;
import com.ayondo.academy.network.share.SocialConstants;
import com.ayondo.academy.persistence.prefs.AuthHeader;
import com.ayondo.academy.persistence.prefs.FirstLaunch;
import com.ayondo.academy.persistence.prefs.ResetHelpScreens;
import com.ayondo.academy.persistence.user.UserProfileCacheRx;
import com.ayondo.academy.utils.Constants;
import com.ayondo.academy.utils.VersionUtils;
import com.ayondo.academy.utils.metrics.MetricsModule;
import com.ayondo.academy.utils.metrics.appsflyer.THAppsFlyer;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.functions.Action1;
import timber.log.Timber;

public class SplashActivity extends BaseActivity
{
    @Inject @FirstLaunch BooleanPreference firstLaunchPreference;
    @Inject @ResetHelpScreens BooleanPreference resetHelpScreens;

    @Inject Lazy<Api> tapStream;
    @Inject MobileAppTracker mobileAppTracker;
    //TODO Change Analytics
    //@Inject
    //Analytics analytics;
    @Inject @AuthHeader String authToken;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject DTOCacheUtilRx dtoCacheUtil;

    @Nullable Subscription userProfileSubscription;
    @Nullable Uri deepLink;

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
        try
        {
            getWindow().getDecorView().findViewById(android.R.id.content).setBackgroundResource(R.drawable.login_bg_1);
        } catch (Throwable e)
        {
            Timber.e(e, "Failed to set guide background");
            getWindow().getDecorView().findViewById(android.R.id.content).setBackgroundColor(
                    getResources().getColor(R.color.authentication_guide_bg_color));
        }

        deepLink = getIntent().getData();

        if (deepLink != null)
        {
            ActivityHelper.launchDashboard(this, deepLink);
        }
    }

    @Override protected void onResume()
    {
        super.onResume();

        //TODO Add code for Google Analytics
        //analytics.tagScreen(AnalyticsConstants.Loading);

        AppEventsLogger.activateApp(this, SocialConstants.FACEBOOK_APP_ID);

        tapStream.get().fireEvent(new Event(getString(Constants.TAP_STREAM_TYPE.openResId), false));

        mobileAppTracker.setReferralSources(this);
        mobileAppTracker.measureSession();

        THAppsFlyer.setAppsFlyerKey(this, MetricsModule.APP_FLYER_KEY);
        THAppsFlyer.sendTracking(this);

        if (!Constants.RELEASE)
        {
            VersionUtils.logScreenMeasurements(this);
        }
        //TODO Add code for Google Analytics
        //analytics.addEvent(new AppLaunchEvent()).addEvent(new SimpleEvent(AnalyticsConstants.LoadingScreen));

        if (firstLaunchPreference.get() || resetHelpScreens.get() || authToken == null)
        {
            ActivityHelper.launchAuthentication(this, deepLink);
            firstLaunchPreference.set(false);
            resetHelpScreens.set(false);
            finish();
        }
        else
        {
            userProfileSubscription = AppObservable.bindActivity(
                    this,
                    userProfileCache.get(currentUserId.toUserBaseKey()))
                    .subscribe(
                            new Action1<Pair<UserBaseKey, UserProfileDTO>>()
                            {
                                @Override public void call(Pair<UserBaseKey, UserProfileDTO> pair)
                                {
                                    dtoCacheUtil.prefetchesUponLogin(pair.second);
                                    ActivityHelper.launchDashboard(SplashActivity.this, deepLink);
                                    finish();
                                }
                            },
                            new Action1<Throwable>()
                            {
                                @Override public void call(Throwable throwable)
                                {
                                    ActivityHelper.launchAuthentication(SplashActivity.this, deepLink);
                                    finish();
                                }
                            });
        }
    }

    @Override protected void onPause()
    {
        if (userProfileSubscription != null)
        {
            userProfileSubscription.unsubscribe();
        }
        super.onPause();
    }

    @Override protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        Uri newDeepLink = intent.getData();
        deepLink = newDeepLink != null ? newDeepLink : deepLink;
    }

    @Override protected boolean requireLogin()
    {
        return false;
    }
}
