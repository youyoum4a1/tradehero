package com.tradehero.th.activities;

import com.tradehero.RobolectricMavenTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class DashboardActivityTest
{
    private DashboardActivity activity;

    @Before public void setUp()
    {
        activity = Robolectric.buildActivity(DashboardActivity.class).create().start().resume().get();
    }

    @Test public void pressBackButtonTwiceWillExitTheApp() throws Exception
    {
        activity.onBackPressed();
        activity.onBackPressed();

        assertThat(activity.isFinishing()).isTrue();
    }
}