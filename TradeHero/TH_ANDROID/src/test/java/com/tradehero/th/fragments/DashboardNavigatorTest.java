package com.ayondo.academy.fragments;

import android.support.v4.app.Fragment;
import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.activities.DashboardActivity;
import com.ayondo.academy.activities.DashboardActivityExtended;
import javax.inject.Inject;
import org.fest.util.VisibleForTesting;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class DashboardNavigatorTest
{
    @Inject DashboardNavigator dashboardNavigator;

    @Before public void setUp()
    {
        DashboardActivity dashboardActivity = Robolectric.buildActivity(DashboardActivityExtended.class).create().visible().get();
        dashboardActivity.inject(this);
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
