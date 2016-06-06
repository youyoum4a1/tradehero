package com.androidth.general.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.androidth.general.fragments.dashboard.RootFragmentType;

public class NavigationAnalyticsReporter implements DashboardNavigator.DashboardFragmentWatcher
{
    //TODO Change Analytics
    //private final Analytics analytics;
    private final DashboardTabHost dashboardTabHost;

    public NavigationAnalyticsReporter( DashboardTabHost dashboardTabHost)
    {
        //TODO Change Analytics
        //Analytics was 1st argument
        //this.analytics = analytics;
        this.dashboardTabHost = dashboardTabHost;
    }

    @Override public <T extends Fragment> void onFragmentChanged(FragmentActivity fragmentActivity, Class<T> fragmentClass, Bundle bundle)
    {
        if (RootFragmentType.values()[dashboardTabHost.getCurrentTab()].fragmentClass == fragmentClass)
        {
            //TODO Change Analytics
            //analytics.fireEvent(new SingleAttributeEvent(RootFragmentType.values()[dashboardTabHost.getCurrentTab()].analyticsString, AnalyticsConstants.ClickedFrom, AnalyticsConstants.Bottom));
        }
        else
        {
            for (RootFragmentType rootFragmentType: RootFragmentType.forLeftDrawer())
            {
                if (rootFragmentType.fragmentClass == fragmentClass)
                {
                    //TODO Change Analytics
                    //analytics.fireEvent(new SingleAttributeEvent(rootFragmentType.analyticsString, AnalyticsConstants.ClickedFrom, AnalyticsConstants.Side));
                    return;
                }
            }
        }
    }
}
