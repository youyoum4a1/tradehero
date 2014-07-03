package com.tradehero.th.fragments.timeline;

import android.os.Bundle;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.utils.THRouter;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class TimelineFragmentTest
{
    private DashboardNavigator dashboardNavigator;
    private UserBaseKey userBaseKey;
    @Inject THRouter thRouter;

    @Before
    public void setUp()
    {
        DashboardActivity activity = Robolectric.setupActivity(DashboardActivity.class);
        dashboardNavigator = activity.getDashboardNavigator();

        userBaseKey = new UserBaseKey(94);
    }

    @After
    public void tearDown()
    {
        dashboardNavigator.popFragment();
    }

    @Test
    public void testShouldShowCorrectUserKey() throws Exception
    {
        Bundle bundle = new Bundle();
        thRouter.save(bundle, userBaseKey);
        Robolectric.getBackgroundScheduler().pause();

        TimelineFragment timelineFragment = dashboardNavigator.pushFragment(PushableTimelineFragment.class, bundle);
        assertThat(timelineFragment.shownUserBaseKey).isEqualTo(userBaseKey);
    }
}
