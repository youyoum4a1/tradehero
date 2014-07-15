package com.tradehero.th.utils.dagger;

import android.content.Context;
import com.localytics.android.LocalyticsSession;
import com.mobileapptracker.MobileAppTracker;
import com.tapstream.sdk.Api;
import com.tapstream.sdk.Config;
import com.tapstream.sdk.Tapstream;
import com.tradehero.th.base.Application;
import com.tradehero.th.fragments.authentication.EmailSignUpFragment;
import com.tradehero.th.fragments.authentication.SignInFragment;
import com.tradehero.th.fragments.authentication.SignUpFragment;
import com.tradehero.th.fragments.leaderboard.filter.LeaderboardFilterSliderContainer;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.metrics.localytics.ForLocalytics;
import com.tradehero.th.utils.metrics.localytics.THLocalyticsSession;
import dagger.Module;
import dagger.Provides;
import java.util.Arrays;
import java.util.List;
import javax.inject.Singleton;

@Module(
        injects = {
                SignInFragment.class,
                SignUpFragment.class,
                EmailSignUpFragment.class,
                LeaderboardFilterSliderContainer.class
        },
        complete = false,
        library = true
)
public class UxModule
{
    private static final String TAPSTREAM_KEY = "Om-yveoZQ7CMU7nUGKlahw";
    private static final String TAPSTREAM_APP_NAME = "tradehero";
    private static final String MAT_APP_ID = "19686";
    private static final String MAT_APP_KEY = "c65b99d5b751944e3637593edd04ce01";
    public static final String TD_APP_ID_KEY = "5991FF8EFB8EFF717C206FCCF9C969A8";

    @Provides @ForLocalytics String provideLocalyticsAppKey()
    {
        return Constants.RELEASE ? Constants.LOCALYTICS_APP_KEY_RELEASE : Constants.LOCALYTICS_APP_KEY_DEBUG;
    }

    // Localytics
    @Provides @Singleton LocalyticsSession provideLocalyticsSession(THLocalyticsSession localyticsSession)
    {
        return localyticsSession;
    }

    @Provides @ForLocalytics List<String> provideLocalyticsPredefineDimensions() {
        return Arrays.asList(
                Constants.TAP_STREAM_TYPE.name()
        );
    }

    // TapStream
    @Provides @Singleton Api provideTapStream(Application app, Config config)
    {
        Tapstream.create(app, TAPSTREAM_APP_NAME, TAPSTREAM_KEY, config);
        return Tapstream.getInstance();
    }

    @Provides @Singleton Config provideTapStreamConfig(Context context)
    {
        Config config = new Config();
        config.setFireAutomaticOpenEvent(false);//this will send twice
        config.setInstallEventName(context.getString(Constants.TAP_STREAM_TYPE.installResId));
        return config;
    }

    // MobileAppTracker
    @Provides @Singleton MobileAppTracker provideMobileAppTracker(Context context)
    {
        MobileAppTracker.init(context, MAT_APP_ID, MAT_APP_KEY);
        MobileAppTracker mobileAppTrackerInstance = MobileAppTracker.getInstance();
        mobileAppTrackerInstance.setPackageName(context.getPackageName() + "." + Constants.TAP_STREAM_TYPE.name());
        mobileAppTrackerInstance.setDebugMode(!Constants.RELEASE);
        return mobileAppTrackerInstance;
    }
}
