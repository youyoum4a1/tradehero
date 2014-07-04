package com.tradehero.th.utils.dagger;

import android.content.Context;
import com.localytics.android.LocalyticsSession;
import com.mobileapptracker.MobileAppTracker;
import com.tapstream.sdk.Config;
import com.tapstream.sdk.Tapstream;
import com.tradehero.th.base.Application;
import com.tradehero.th.fragments.authentication.EmailSignUpFragment;
import com.tradehero.th.fragments.authentication.SignInFragment;
import com.tradehero.th.fragments.authentication.SignUpFragment;
import com.tradehero.th.fragments.leaderboard.filter.LeaderboardFilterSliderContainer;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.metrics.localytics.LocalyticAppKey;
import com.tradehero.th.utils.metrics.localytics.THLocalyticsSession;
import dagger.Module;
import dagger.Provides;
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

    @Provides @LocalyticAppKey String provideLocalyticsAppKey()
    {
        return Constants.RELEASE ? Constants.LOCALYTICS_APP_KEY_RELEASE : Constants.LOCALYTICS_APP_KEY_DEBUG;
    }

    // localytics
    @Provides @Singleton LocalyticsSession provideLocalyticsSession(THLocalyticsSession localyticsSession)
    {
        return localyticsSession;
    }

    // TapStream
    @Provides @Singleton Tapstream provideTapStream(Application app, Config config)
    {
        Tapstream.create(app, TAPSTREAM_APP_NAME, TAPSTREAM_KEY, config);
        return Tapstream.getInstance();
    }

    @Provides @Singleton Config provideTapStreamConfig(Context context)
    {
        Config config = new Config();
        config.setFireAutomaticOpenEvent(false);//this will send twice
        config.setInstallEventName(context.getString(
                Constants.TAP_STREAM_TYPE.installResId));
        return config;
    }

    //MAT
    @Provides @Singleton MobileAppTracker provideMAT(Context context)
    {
        MobileAppTracker.init(context, MAT_APP_ID, MAT_APP_KEY);
        return MobileAppTracker.getInstance();
    }
}
