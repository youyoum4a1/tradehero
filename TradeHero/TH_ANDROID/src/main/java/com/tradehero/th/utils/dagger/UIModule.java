package com.tradehero.th.utils.dagger;

import com.tradehero.th.fragments.leaderboard.LeaderboardMarkUserListViewFragment;
import com.tradehero.th.fragments.timeline.TimelineItemView;
import dagger.Module;
import dagger.Provides;
import org.ocpsoft.prettytime.PrettyTime;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/27/14 Time: 11:48 AM Copyright (c) TradeHero
 */
@Module(
        injects = {
                TimelineItemView.class,
                LeaderboardMarkUserListViewFragment.class,
        },
        complete = false
)
public class UIModule
{
    @Provides PrettyTime providePrettyTime()
    {
        return new PrettyTime();
    }
}
