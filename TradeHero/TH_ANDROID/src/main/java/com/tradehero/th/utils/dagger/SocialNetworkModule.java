package com.tradehero.th.utils.dagger;

import android.content.Context;
import com.tradehero.th.R;
import com.tradehero.th.activities.AuthenticationActivity;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.utils.FacebookUtils;
import com.tradehero.th.utils.LinkedInUtils;
import com.tradehero.th.utils.TwitterUtils;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/27/14 Time: 11:42 AM Copyright (c) TradeHero
 */
@Module(
        injects = {
                AuthenticationActivity.class,
                DashboardActivity.class,
        },
        complete = false
)
public class SocialNetworkModule
{
    @Provides @Singleton FacebookUtils provideFacebookUtils(Context context)
    {
        return new FacebookUtils(context, context.getString(R.string.FACEBOOK_APP_ID));
    }

    @Provides @Singleton TwitterUtils provideTwitterUtils(Context context)
    {
        return new TwitterUtils(
                context.getString(R.string.TWITTER_CONSUMER_KEY),
                context.getString(R.string.TWITTER_CONSUMER_SECRET));
    }

    @Provides @Singleton LinkedInUtils provideLinkedInUtils(Context context)
    {
        return new LinkedInUtils(
                context.getString(R.string.LINKEDIN_CONSUMER_KEY),
                context.getString(R.string.LINKEDIN_CONSUMER_SECRET));
    }
}
