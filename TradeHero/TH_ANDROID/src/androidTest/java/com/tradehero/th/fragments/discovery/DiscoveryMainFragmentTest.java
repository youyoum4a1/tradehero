package com.tradehero.th.fragments.discovery;

import android.app.ActionBar;
import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.activities.DashboardActivityExtended;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
public class DiscoveryMainFragmentTest
{
    @Inject DashboardNavigator navigator;
    private DiscoveryMainFragment discoveryMainFragment;
    private DashboardActivity activity;

    @Before public void setUp()
    {
        activity = Robolectric.setupActivity(DashboardActivityExtended.class);
        activity.inject(this);
        discoveryMainFragment = navigator.goToTab(RootFragmentType.DISCOVERY);
    }

    @After public void tearDown()
    {
        navigator.popFragment();
    }

    @Test public void dashboardFragmentShouldHaveSubTabs()
    {
        assertThat(discoveryMainFragment).isNotNull();
        assertThat(activity.getActionBar()).isNotNull();
        assertThat(activity.getActionBar().getNavigationMode()).isEqualTo(ActionBar.NAVIGATION_MODE_TABS);
        assertThat(activity.getActionBar().getTabCount()).isEqualTo(DiscoveryTabType.values().length);
    }

    @Test public void clickOnTabShouldShowCorrectFragment()
    {
        ActionBar actionBar = activity.getActionBar();

        for (int clickingTabIndex = 0; clickingTabIndex < DiscoveryTabType.values().length; ++clickingTabIndex)
        {
            actionBar.selectTab(actionBar.getTabAt(clickingTabIndex));
            assertThat(discoveryMainFragment.tabViewPager.getCurrentItem()).isEqualTo(clickingTabIndex);
        }
    }
}