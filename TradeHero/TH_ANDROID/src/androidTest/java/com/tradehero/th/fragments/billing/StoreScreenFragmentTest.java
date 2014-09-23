package com.tradehero.th.fragments.billing;

import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.activities.DashboardActivityExtended;
import com.tradehero.th.fragments.DashboardNavigator;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowToast;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
public class StoreScreenFragmentTest
{
    @Inject DashboardNavigator dashboardNavigator;

    @Before public void setUp()
    {
        DashboardActivity activity = Robolectric.setupActivity(DashboardActivityExtended.class);
        activity.inject(this);
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