package com.tradehero.th.utils.dagger;

import android.content.Context;
import com.tradehero.common.cache.LruMemFileCache;
import com.tradehero.th.fragments.alert.AlertEditFragment;
import com.tradehero.th.fragments.alert.AlertItemView;
import com.tradehero.th.fragments.alert.AlertListItemAdapter;
import com.tradehero.th.fragments.alert.AlertViewFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardCommunityAdapter;
import com.tradehero.th.fragments.leaderboard.LeaderboardDefListAdapter;
import com.tradehero.th.fragments.settings.SettingsPayPalFragment;
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
                AlertEditFragment.class,

                LeaderboardDefListAdapter.class,
                LeaderboardCommunityAdapter.class,
        },
        complete = false,
        library = true
)
public class CacheModule
{
    @Provides @Singleton LruMemFileCache provideLruMemFileCache(Context context)
    {
        return new LruMemFileCache(context);
    }
}
