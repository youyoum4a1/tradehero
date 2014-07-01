package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.os.Bundle;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.DashboardNavigator;
import java.util.Random;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class MeTimelineFragmentTest
{
    @Inject Context context;
    @Inject CurrentUserId currentUserId;
    private DashboardNavigator dashboardNavigator;
    private MeTimelineFragment meTimelineFragment;

    @Before
    public void setUp()
    {
        DashboardActivity activity = Robolectric.setupActivity(DashboardActivity.class);
        dashboardNavigator = activity.getDashboardNavigator();
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
        Robolectric.getBackgroundScheduler().pause();
        meTimelineFragment = dashboardNavigator.pushFragment(MeTimelineFragment.class, new Bundle());
        assertThat(meTimelineFragment.shownUserBaseKey.key).isEqualTo(userId);
    }
}
