package com.tradehero.th.utils.dagger;

import android.content.Context;
import android.content.SharedPreferences;
import com.tradehero.common.cache.LruMemFileCache;
import com.tradehero.th.fragments.alert.AlertItemView;
import com.tradehero.th.fragments.alert.AlertListItemAdapter;
import com.tradehero.th.fragments.alert.AlertViewFragment;
import com.tradehero.th.fragments.competition.LeaderboardCompetitionView;
import com.tradehero.th.fragments.leaderboard.LeaderboardCommunityAdapter;
import com.tradehero.th.fragments.leaderboard.LeaderboardDefListAdapter;
import com.tradehero.th.fragments.settings.SettingsPayPalFragment;
import com.tradehero.th.fragments.trending.ExtraTileAdapter;
import com.tradehero.th.fragments.trending.ProviderTileView;
import com.tradehero.th.models.alert.SecurityAlertAssistant;
import com.tradehero.th.persistence.portfolio.OwnedPortfolioFetchAssistant;
import com.tradehero.th.persistence.user.UserProfileFetchAssistant;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/27/14 Time: 11:39 AM Copyright (c) TradeHero
 */
@Module(
        injects = {
                UserProfileFetchAssistant.class,
                OwnedPortfolioFetchAssistant.class,
                SecurityAlertAssistant.class,
                SettingsPayPalFragment.class,

                AlertListItemAdapter.class,
                AlertItemView.class,
                AlertViewFragment.class,

                LeaderboardDefListAdapter.class,
                LeaderboardCommunityAdapter.class,
                LeaderboardCompetitionView.class,

                // Extra Tile needs to know about userProfile data for survey tile element
                ExtraTileAdapter.class,
                ProviderTileView.class,
        },
        complete = false,
        library = true
)
public class CacheModule
{
    private static final String PREFERENCE_KEY = "th";

    @Provides @Singleton LruMemFileCache provideLruMemFileCache(Context context)
    {
        return new LruMemFileCache(context);
    }

    @Provides @Singleton SharedPreferences provideSharePreferences(Context context)
    {
        return context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE);
    }
}
