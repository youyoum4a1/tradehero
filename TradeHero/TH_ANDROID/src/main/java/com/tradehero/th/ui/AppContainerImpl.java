package com.tradehero.th.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;
import com.tradehero.th.R;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import com.tradehero.th.utils.DeviceUtil;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import static butterknife.ButterKnife.findById;

public class AppContainerImpl implements AppContainer
{
    private final ResideMenu.OnMenuListener menuListener;
    private final ResideMenu resideMenu;
    private Activity activity;

    @Inject public AppContainerImpl(
            ResideMenu resideMenu,
            ResideMenuListener menuListener)
    {
        this.resideMenu = resideMenu;
        this.menuListener = menuListener;
    }

    @Override public ViewGroup get(final Activity activity)
    {
        //activity.setContentView(R.layout.residemenu_main);
        this.activity = activity;
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

        List<View> menuItems = new ArrayList<>();
        for (DashboardTabType tabType : DashboardTabType.usableValues())
        {
            if (tabType.show)
            {
                View menuItem = createMenuItemFromTabType(activity, tabType);
                menuItem.setTag(tabType);
                menuItem.setOnClickListener(menuItemClickListener);
                menuItems.add(menuItem);
            }
        }
        resideMenu.setMenuListener(new CustomOnMenuListener());
        resideMenu.setMenuItems(menuItems);

        if (activity instanceof OnResideMenuItemClickListener)
        {
            mOnResideMenuItemClickListener = ((OnResideMenuItemClickListener) activity);
        }
        return findById(activity, android.R.id.content);
    }

    private OnResideMenuItemClickListener mOnResideMenuItemClickListener;

    public interface OnResideMenuItemClickListener
    {
        void onResideMenuItemClick(DashboardTabType tabType);
    }

    class CustomOnMenuListener implements ResideMenu.OnMenuListener
    {
        @Override public void openMenu()
        {
            closeSoftInput();
        }

        @Override public void closeMenu()
        {
        }
    }

    private void closeSoftInput()
    {
        try
        {
            DeviceUtil.dismissKeyboard(activity);
        }
        catch (Exception e)
        {
        }
    }

    /**
     * TODO this is a hack due to time constraint
     */
    private View createMenuItemFromTabType(Context context, DashboardTabType tabType)
    {
        if (tabType.hasCustomView())
        {
            LayoutInflater inflater = LayoutInflater.from(context);
            return inflater.inflate(tabType.viewResId, null);
        }
        else
        {
            return new ResideMenuItem(context, tabType.drawableResId, tabType.stringResId);
        }
    }

    private class ResideMenuItemClickListener implements View.OnClickListener
    {
        @Override public void onClick(View v)
        {
            resideMenu.closeMenu();
        }
    }
}
