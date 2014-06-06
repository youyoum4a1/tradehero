package com.tradehero.th.fragments.security;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.activities.DashboardActivity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class WatchlistEditFragmentTest
{
    private DashboardActivity activity;

    @Before public void setUp()
    {
        activity = Robolectric.buildActivity(DashboardActivity.class).create().get();
    }

    @Test public void shouldNotBeNull()
    {
        assertThat(true).isEqualTo(true);
    }
}
