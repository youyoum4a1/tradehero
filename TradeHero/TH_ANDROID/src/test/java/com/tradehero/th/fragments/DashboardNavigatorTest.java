package com.tradehero.th.fragments;

import android.support.v4.app.Fragment;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.activities.DashboardActivity;
import org.fest.util.VisibleForTesting;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class DashboardNavigatorTest
{
    private DashboardNavigator dashboardNavigator;

    @Before public void setUp()
    {
        DashboardActivity dashboardActivity = Robolectric.buildActivity(DashboardActivity.class).create().visible().get();
        dashboardNavigator = dashboardActivity.getDashboardNavigator();
    }

    @After public void tearDown()
    {
        dashboardNavigator.popFragment();
        dashboardNavigator = null;
    }

    @Test public void shouldAbleToNavigateBetweenFragments()
    {
        dashboardNavigator.pushFragment(TestFragment1.class);
        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(TestFragment1.class);

        dashboardNavigator.pushFragment(TestFragment2.class);
        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(TestFragment2.class);

        dashboardNavigator.popFragment();
        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(TestFragment1.class);
    }

    @VisibleForTesting
    public static class TestFragment1 extends Fragment {}
    @VisibleForTesting
    public static class TestFragment2 extends Fragment {}
}
