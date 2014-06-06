package com.tradehero.th.fragments.security;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.internal.ActionBarSherlockCompat;
import com.actionbarsherlock.internal.ActionBarSherlockNative;
import com.actionbarsherlock.internal.ActionBarSherlockRobolectric;
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
        ActionBarSherlock.registerImplementation(ActionBarSherlockRobolectric.class);
        ActionBarSherlock.unregisterImplementation(ActionBarSherlockNative.class);
        ActionBarSherlock.unregisterImplementation(ActionBarSherlockCompat.class);

        activity = Robolectric.buildActivity(DashboardActivity.class).create().visible().get();
    }

    @Test public void shouldNotBeNull()
    {
        assertThat(activity).isNotNull();
    }
}
