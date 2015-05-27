package com.tradehero.th.ui;

import android.support.v4.widget.DrawerLayout;
import android.view.View;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import dagger.Lazy;
import javax.inject.Inject;

public class LeftDrawerMenuItemClickListener implements View.OnClickListener
{
    private final DrawerLayout drawerLayout;
    private final Lazy<DashboardNavigator> navigator;

    @Inject LeftDrawerMenuItemClickListener(DrawerLayout drawerLayout, Lazy<DashboardNavigator> navigator)
    {
        this.drawerLayout = drawerLayout;
        this.navigator = navigator;
    }

    @Override public void onClick(View view)
    {
        Object tag = view.getTag();
        if (tag instanceof RootFragmentType)
        {
            RootFragmentType tabType = (RootFragmentType) tag;
            if (tabType.fragmentClass != null)
            {
                navigator.get().goToTab(tabType);
                drawerLayout.closeDrawers();
            }
            else if (tabType.activityClass != null)
            {
                navigator.get().launchTabActivity(tabType);
            }
        }
    }
}
