package com.tradehero.th.ui;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;
import com.tradehero.th.R;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import javax.inject.Inject;

import static butterknife.ButterKnife.findById;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/5/14 Time: 4:07 PM Copyright (c) TradeHero
 */
public class AppContainerImpl implements AppContainer
{
    private final ResideMenu.OnMenuListener menuListener;
    private final ResideMenu resideMenu;

    @Inject public AppContainerImpl(
            ResideMenu resideMenu,
            ResideMenuListener menuListener)
    {
        this.resideMenu = resideMenu;
        this.menuListener = menuListener;
    }

    @Override public ViewGroup get(final Activity activity)
    {
        activity.setContentView(R.layout.dashboard_with_bottom_bar);

        resideMenu.setBackground(R.drawable.parallax_bg);
        resideMenu.attachTo((ViewGroup) activity.getWindow().getDecorView());

        // hAcK to make the menu works while waiting for a injectable navigator
        ResideMenuItemClickListener menuItemClickListener = new ResideMenuItemClickListener()
        {
            @Override public void onClick(View v)
            {
                super.onClick(v);
                if (activity instanceof DashboardNavigatorActivity && !activity.isFinishing())
                {
                    Object tag = v.getTag();
                    if (tag instanceof DashboardTabType)
                    {
                        DashboardTabType tabType = (DashboardTabType) tag;
                        ((DashboardNavigatorActivity) activity).getDashboardNavigator().goToTab(tabType);
                    }
                }
            }
        };

        for (DashboardTabType tabType: DashboardTabType.values())
        {
            ResideMenuItem menuItem = new ResideMenuItem(activity, tabType.drawableResId, tabType.stringResId);
            menuItem.setTag(tabType);
            resideMenu.addMenuItem(menuItem);
            menuItem.setOnClickListener(menuItemClickListener);
        }

        return findById(activity, android.R.id.content);
    }

    private class ResideMenuItemClickListener implements View.OnClickListener
    {
        @Override public void onClick(View v)
        {
            resideMenu.closeMenu();
        }
    }
}
