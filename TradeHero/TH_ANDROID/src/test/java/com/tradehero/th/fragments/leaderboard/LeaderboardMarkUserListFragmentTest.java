package com.ayondo.academy.fragments.leaderboard;

import android.os.Bundle;
import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.activities.DashboardActivityExtended;
import com.ayondo.academy.api.leaderboard.key.LeaderboardDefKey;
import com.ayondo.academy.fragments.DashboardNavigator;
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
    private LeaderboardMarkUserRecyclerFragment leaderboardMarkUserRecyclerFragment;

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
        leaderboardMarkUserRecyclerFragment = null;
    }

    @Test public void handleFollowRequestedCallsAlertDialog()
    {
        Robolectric.getBackgroundThreadScheduler().pause();
        Bundle args = new Bundle();
        LeaderboardMarkUserRecyclerFragment.putLeaderboardDefKey(args, new LeaderboardDefKey(123));
        leaderboardMarkUserRecyclerFragment = dashboardNavigator.pushFragment(LeaderboardMarkUserRecyclerFragment.class, args);
    }
}
