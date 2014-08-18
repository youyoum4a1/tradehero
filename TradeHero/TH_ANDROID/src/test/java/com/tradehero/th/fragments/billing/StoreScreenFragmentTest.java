package com.tradehero.th.fragments.billing;

import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowToast;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
public class StoreScreenFragmentTest
{
    private DashboardNavigator dashboardNavigator;

    @Before public void setUp()
    {
        DashboardActivity activity = Robolectric.setupActivity(DashboardActivity.class);
        dashboardNavigator = activity.getDashboardNavigator();
    }

    @Test public void shouldNotToastAnythingOnStartUp()
    {
        ShadowToast.reset();
        StoreScreenFragment storeScreenFragment = dashboardNavigator.pushFragment(StoreScreenFragment.class);
        assertThat(storeScreenFragment).isNotNull();

        String latestToastText = ShadowToast.getTextOfLatestToast();
        assertThat(latestToastText).isNull();
    }
}