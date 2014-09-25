package com.tradehero.th.ui;

import android.view.View;
import com.special.residemenu.ResideMenu;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import dagger.Lazy;
import javax.inject.Inject;

public class ResideMenuItemClickListener implements View.OnClickListener
{
    private final ResideMenu resideMenu;
    private final Lazy<DashboardNavigator> navigator;

    @Inject ResideMenuItemClickListener(ResideMenu resideMenu, Lazy<DashboardNavigator> navigator)
    {
        this.resideMenu = resideMenu;
        this.navigator = navigator;
    }

    @Override public void onClick(View view)
    {
        resideMenu.closeMenu();
        Object tag = view.getTag();
        if (tag instanceof RootFragmentType)
        {
            RootFragmentType tabType = (RootFragmentType) tag;
            if (tabType != RootFragmentType.DIVIDER)
            {
                navigator.get().goToTab(tabType);
            }
        }
    }
}
