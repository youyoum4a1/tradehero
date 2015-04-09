package com.tradehero.th.fragments.timeline;

import android.os.Bundle;
import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.BuildConfig;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.activities.DashboardActivityExtended;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.DashboardNavigator;
import java.util.Random;
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
public class MeTimelineFragmentTest
{
    @Inject CurrentUserId currentUserId;
    @Inject DashboardNavigator dashboardNavigator;
    private MeTimelineFragment meTimelineFragment;

    @Before
    public void setUp()
    {
        DashboardActivity activity = Robolectric.setupActivity(DashboardActivityExtended.class);
        activity.inject(this);
    }

    @After
    public void tearDown()
    {
        dashboardNavigator.popFragment();
        meTimelineFragment = null;
    }

    @Test(expected = RuntimeException.class)
    public void testOnCreateCrashOnNullArgs()
    {
        dashboardNavigator.pushFragment(MeTimelineFragment.class, null);
    }

    @Test public void testOnCreatePutsCurrentUserId()
    {
        int userId = (int) new Random().nextLong();
        currentUserId.set(userId);
        Robolectric.getBackgroundThreadScheduler().pause();
        meTimelineFragment = dashboardNavigator.pushFragment(MeTimelineFragment.class, new Bundle());
        assertThat(meTimelineFragment.shownUserBaseKey.key).isEqualTo(userId);
    }
}
