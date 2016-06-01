package com.ayondo.academy.fragments.billing;

import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.activities.DashboardActivity;
import com.ayondo.academy.activities.DashboardActivityExtended;
import com.ayondo.academy.fragments.DashboardNavigator;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
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