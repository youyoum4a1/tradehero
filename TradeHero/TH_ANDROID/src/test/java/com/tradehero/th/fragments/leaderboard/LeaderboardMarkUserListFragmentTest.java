package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.BuildConfig;
import com.tradehero.th.activities.DashboardActivityExtended;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.fragments.DashboardNavigator;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class LeaderboardMarkUserListFragmentTest
{
    @Inject DashboardNavigator dashboardNavigator;
    private LeaderboardMarkUserListFragment leaderboardMarkUserListFragment;

    @Before public void setUp()
    {
        Robolectric.setupActivity(DashboardActivityExtended.class).inject(this);
    }

    @After public void tearDown()
    {
        if (dashboardNavigator != null)
        {
            dashboardNavigator.popFragment();
        }
        dashboardNavigator = null;
        leaderboardMarkUserListFragment = null;
    }

    @Test public void handleFollowRequestedCallsAlertDialog()
    {
        Robolectric.getBackgroundThreadScheduler().pause();
        Bundle args = new Bundle();
        LeaderboardMarkUserListFragment.putLeaderboardDefKey(args, new LeaderboardDefKey(123));
        leaderboardMarkUserListFragment = dashboardNavigator.pushFragment(LeaderboardMarkUserListFragment.class, args);
    }
}
