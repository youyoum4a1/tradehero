package com.tradehero.th.ui;

import com.tradehero.th.fragments.leaderboard.LeaderboardMarkUserListFragment;
import com.tradehero.th.fragments.timeline.TimelineItemView;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationItemView;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationsView;
import com.tradehero.th.fragments.updatecenter.messages.MessagesCenterFragment;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationsCenterFragment;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.ocpsoft.prettytime.PrettyTime;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/27/14 Time: 11:48 AM Copyright (c) TradeHero
 */
@Module(
        includes = {
                UIComponents.class
        },
        injects = {
                TimelineItemView.class,
                LeaderboardMarkUserListFragment.class,
                UpdateCenterFragment.class,
                NotificationsView.class,
                NotificationItemView.class,

                UpdateCenterFragment.class,
                MessagesCenterFragment.class,
                NotificationsCenterFragment.class,
        },
        complete = false,
        library = true
)
public class UIModule
{
    @Provides PrettyTime providePrettyTime()
    {
        return new PrettyTime();
    }

    @Provides @Singleton AppContainer provideAppContainer()
    {
        return AppContainer.DEFAULT;
    }

    @Provides @Singleton ViewWrapper provideViewWrapper()
    {
        return ViewWrapper.DEFAULT;
    }
}
