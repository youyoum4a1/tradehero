package com.ayondo.academy.fragments.timeline;

import android.os.Bundle;
import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.activities.DashboardActivity;
import com.ayondo.academy.activities.DashboardActivityExtended;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.fragments.DashboardNavigator;
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
