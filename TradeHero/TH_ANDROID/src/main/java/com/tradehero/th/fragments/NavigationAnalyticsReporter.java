package com.tradehero.th.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SingleAttributeEvent;
@Deprecated
public class NavigationAnalyticsReporter implements DashboardNavigator.DashboardFragmentWatcher
{
    private final Analytics analytics;
    private final DashboardTabHost dashboardTabHost;

    public NavigationAnalyticsReporter(Analytics analytics, DashboardTabHost dashboardTabHost)
    {
        this.analytics = analytics;
        this.dashboardTabHost = dashboardTabHost;
    }

    @Override public <T extends Fragment> void onFragmentChanged(FragmentActivity fragmentActivity, Class<T> fragmentClass, Bundle bundle)
    {
        if (RootFragmentType.values()[dashboardTabHost.getCurrentTab()].fragmentClass == fragmentClass)
        {
            analytics.fireEvent(new SingleAttributeEvent(
                    RootFragmentType.values()[dashboardTabHost.getCurrentTab()].analyticsString,
                    AnalyticsConstants.ClickedFrom, AnalyticsConstants.Bottom));
        }
        else
        {
            for (RootFragmentType rootFragmentType: RootFragmentType.forResideMenu())
            {
                if (rootFragmentType.fragmentClass == fragmentClass)
                {
                    analytics.fireEvent(new SingleAttributeEvent(rootFragmentType.analyticsString,
                            AnalyticsConstants.ClickedFrom, AnalyticsConstants.Side));
                    return;
                }
            }
        }
    }
}
