package com.tradehero.th.fragments.discovery;

import com.actionbarsherlock.app.ActionBar;
import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
public class DiscoveryMainFragmentTest
{
    private DashboardNavigator navigator;
    private DiscoveryMainFragment discoveryMainFragment;
    private DashboardActivity activity;

    @Before public void setUp()
    {
        activity = Robolectric.setupActivity(DashboardActivity.class);
        navigator = activity.getDashboardNavigator();
        discoveryMainFragment = navigator.goToTab(RootFragmentType.DISCOVERY);
    }

    @After public void tearDown()
    {
        navigator.popFragment();
    }

    @Test public void dashboardFragmentShouldHaveSubTabs()
    {
        assertThat(discoveryMainFragment).isNotNull();
        assertThat(activity.getSupportActionBar()).isNotNull();
        assertThat(activity.getSupportActionBar().getNavigationMode()).isEqualTo(ActionBar.NAVIGATION_MODE_TABS);
        assertThat(activity.getSupportActionBar().getTabCount()).isEqualTo(DiscoveryTabType.values().length);
    }

    @Test public void clickOnTabShouldShowCorrectFragment()
    {
        ActionBar actionBar = activity.getSupportActionBar();

        for (int clickingTabIndex = 0; clickingTabIndex < DiscoveryTabType.values().length; ++clickingTabIndex)
        {
            actionBar.selectTab(actionBar.getTabAt(clickingTabIndex));
            assertThat(discoveryMainFragment.mViewPager.getCurrentItem()).isEqualTo(clickingTabIndex);
        }
    }
}