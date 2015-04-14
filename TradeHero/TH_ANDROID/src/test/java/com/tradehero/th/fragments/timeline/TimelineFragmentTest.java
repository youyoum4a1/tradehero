package com.tradehero.th.fragments.timeline;

import android.os.Bundle;
import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.BuildConfig;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.activities.DashboardActivityExtended;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.utils.route.THRouter;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class TimelineFragmentTest
{
    @Inject DashboardNavigator dashboardNavigator;
    private UserBaseKey userBaseKey;
    @Inject THRouter thRouter;

    @Before
    public void setUp()
    {
        DashboardActivity activity = Robolectric.setupActivity(DashboardActivityExtended.class);
        activity.inject(this);

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
        Robolectric.getBackgroundThreadScheduler().pause();

        TimelineFragment timelineFragment = dashboardNavigator.pushFragment(PushableTimelineFragment.class, bundle);
        assertThat(timelineFragment.shownUserBaseKey).isEqualTo(userBaseKey);
    }
}
