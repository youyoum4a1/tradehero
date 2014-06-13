package com.tradehero.th.utils;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class THRouterTest
{
    private static final String USER_PROFILE_ROUTE = "user/:userId";

    private DashboardActivity activity;
    private DashboardNavigator dashboardNavigator;
    private THRouter thRouter;

    @Before public void setUp()
    {
        activity = Robolectric.setupActivity(DashboardActivity.class);
        dashboardNavigator = activity.getDashboardNavigator();

        thRouter = new THRouter(activity);
    }

    @Test public void shouldOpenUserTimelineForUserProfileRoute()
    {
        thRouter.mapFragment(USER_PROFILE_ROUTE, PushableTimelineFragment.class);

        thRouter.open("user/108805");

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(PushableTimelineFragment.class);
    }
}