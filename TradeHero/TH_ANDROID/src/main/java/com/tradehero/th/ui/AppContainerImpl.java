package com.tradehero.th.ui;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.Toast;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;
import com.tradehero.th.R;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/5/14 Time: 4:07 PM Copyright (c) TradeHero
 */
public class AppContainerImpl implements AppContainer
{
    private ResideMenu.OnMenuListener menuListener;

    @Inject public AppContainerImpl(ResideMenuListener menuListener)
    {
        this.menuListener = menuListener;
    }

    @Override public ViewGroup get(Activity activity)
    {
        ResideMenu resideMenu = new ResideMenu(activity);
        //resideMenu.setBackground(R.drawable.menu_background);
        resideMenu.attachToActivity(activity);
        resideMenu.setMenuListener(menuListener);

        for (DashboardTabType tabType: DashboardTabType.values())
        {
            ResideMenuItem menuItem = new ResideMenuItem(activity, tabType.drawableResId, tabType.stringResId);
            resideMenu.addMenuItem(menuItem);
            //menuItem.setOnClickListener();
        }

        return (ViewGroup) activity.findViewById(android.R.id.content);
    }
}
