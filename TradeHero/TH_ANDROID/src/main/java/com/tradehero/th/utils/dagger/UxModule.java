package com.tradehero.th.utils.dagger;

import android.content.Context;
import com.localytics.android.LocalyticsSession;
import com.tapstream.sdk.Config;
import com.tapstream.sdk.Tapstream;
import com.tradehero.th.base.Application;
import com.tradehero.th.fragments.authentication.EmailSignUpFragment;
import com.tradehero.th.fragments.authentication.SignInFragment;
import com.tradehero.th.fragments.authentication.SignUpFragment;
import com.tradehero.th.fragments.leaderboard.filter.LeaderboardFilterSliderContainer;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/7/14 Time: 11:22 AM Copyright (c) TradeHero
 */
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
    private static final String TABSTREAM_KEY = "Om-yveoZQ7CMU7nUGKlahw";
    private static final String TABSTREAM_APP_NAME = "tradehero";

    // localytics
    @Provides @Singleton LocalyticsSession provideLocalyticsSession(Context context)
    {
        return new LocalyticsSession(context);
    }

    // tabstream

    @Provides @Singleton Tapstream provideTabStream(Application app, Config config)
    {
        Tapstream.create(app, TABSTREAM_APP_NAME, TABSTREAM_KEY, config);
        return Tapstream.getInstance();
    }

    @Provides @Singleton Config provideTabStreamConfig()
    {
        Config config = new Config();
        config.setFireAutomaticOpenEvent(false);//this will send twice
        return config;
    }

}
