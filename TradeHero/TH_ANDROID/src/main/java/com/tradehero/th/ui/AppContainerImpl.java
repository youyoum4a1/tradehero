package com.tradehero.th.ui;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
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

    @Override public ViewGroup get(Activity activity)
    {
        activity.setContentView(R.layout.residemenu_main);

        resideMenu.setBackground(R.drawable.parallax_bg);
        resideMenu.attachTo((ViewGroup) activity.getWindow().getDecorView());
        //resideMenu.setMenuListener();
        ResideMenuItemClickListener menuItemClickListener = new ResideMenuItemClickListener();

        for (DashboardTabType tabType: DashboardTabType.values())
        {
            ResideMenuItem menuItem = new ResideMenuItem(activity, tabType.drawableResId, tabType.stringResId);
            menuItem.setTag(tabType);
            resideMenu.addMenuItem(menuItem);
            menuItem.setOnClickListener(menuItemClickListener);
        }

        if (activity instanceof OnResideMenuItemClickListener) {
            mOnResideMenuItemClickListener = ((OnResideMenuItemClickListener)activity);
        }
        return findById(activity, android.R.id.content);
    }


    private OnResideMenuItemClickListener mOnResideMenuItemClickListener;
    public interface OnResideMenuItemClickListener {
        void onResideMenuItemClick(DashboardTabType tabType);
    }



    private class ResideMenuItemClickListener implements View.OnClickListener
    {
        @Override public void onClick(View v)
        {
            DashboardTabType tabType = (DashboardTabType)v.getTag();
            switch (tabType) {
                case TRENDING:
                    break;
                case COMMUNITY:
                    break;
                case STORE:
                    break;
                case PORTFOLIO:
                    break;
                case TIMELINE:
                    break;
                default:
                    return;
            }
            mOnResideMenuItemClickListener.onResideMenuItemClick(tabType);
            resideMenu.closeMenu();

        }

    }
}
