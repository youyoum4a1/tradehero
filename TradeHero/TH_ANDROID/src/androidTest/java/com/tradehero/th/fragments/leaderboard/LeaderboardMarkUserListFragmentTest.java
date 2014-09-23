package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.activities.DashboardActivityExtended;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.social.hero.HeroAlertDialogUtil;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

@RunWith(THRobolectricTestRunner.class)
public class LeaderboardMarkUserListFragmentTest
{
    @Inject DashboardNavigator dashboardNavigator;
    private LeaderboardMarkUserListFragment leaderboardMarkUserListFragment;
    private HeroAlertDialogUtil heroAlertDialogUtil;

    @Before public void setUp()
    {
        DashboardActivity activity = Robolectric.setupActivity(DashboardActivityExtended.class);
        activity.inject(this);
    }

    @After public void tearDown()
    {
        if (dashboardNavigator != null)
        {
            dashboardNavigator.popFragment();
        }
        dashboardNavigator = null;
        leaderboardMarkUserListFragment = null;
        heroAlertDialogUtil = null;
    }

    @Test public void handleFollowRequestedCallsAlertDialog()
    {
        Robolectric.getBackgroundScheduler().pause();
        Bundle args = new Bundle();
        LeaderboardMarkUserListFragment.putLeaderboardDefKey(args, new LeaderboardDefKey(123));
        leaderboardMarkUserListFragment = dashboardNavigator.pushFragment(LeaderboardMarkUserListFragment.class, args);
    }
}
