package com.tradehero.th.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.special.residemenu.ResideMenu;
import com.tradehero.common.widget.reside.THResideMenuItemImpl;
import com.tradehero.th.R;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import com.tradehero.th.utils.DeviceUtil;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import static butterknife.ButterKnife.findById;

public class AppContainerImpl implements AppContainer
{
    private final ResideMenu resideMenu;
    private final ResideMenuItemClickListener resideMenuItemClickListener;
    private Activity activity;

    @Inject public AppContainerImpl(ResideMenu resideMenu, ResideMenuItemClickListener resideMenuItemClickListener)
    {
        this.resideMenu = resideMenu;
        this.resideMenuItemClickListener = resideMenuItemClickListener;
    }

    @Override public ViewGroup get(final Activity activity)
    {
        this.activity = activity;
        activity.setContentView(R.layout.dashboard_with_bottom_bar);

        resideMenu.setBackground(R.drawable.parallax_bg);
        resideMenu.attachTo((ViewGroup) activity.getWindow().getDecorView());

        List<View> menuItems = new ArrayList<>();
        for (RootFragmentType tabType : RootFragmentType.forResideMenu())
        {
            View menuItem = createMenuItemFromTabType(activity, tabType);
            menuItem.setOnClickListener(resideMenuItemClickListener);
            menuItems.add(menuItem);
        }
        resideMenu.setMenuListener(new CustomOnMenuListener());
        resideMenu.setMenuItems(menuItems);

        // only enable swipe from right to left
        resideMenu.setEnableSwipeLeftToRight(false);
        resideMenu.setEnableSwipeRightToLeft(true);

        return findById(activity, android.R.id.content);
    }

    class CustomOnMenuListener implements ResideMenu.OnMenuListener
    {
        @Override public void openMenu()
        {
            DeviceUtil.dismissKeyboard(activity);
            if (activity instanceof ResideMenu.OnMenuListener && !activity.isFinishing())
            {
                ((ResideMenu.OnMenuListener) activity).openMenu();
            }
        }

        @Override public void closeMenu()
        {
            if (activity instanceof ResideMenu.OnMenuListener && !activity.isFinishing())
            {
                ((ResideMenu.OnMenuListener) activity).closeMenu();
            }
        }
    }

    /**
     * TODO this is a hack due to time constraint
     */
    private View createMenuItemFromTabType(Context context, RootFragmentType tabType)
    {
        View created;
        if (tabType.hasCustomView())
        {
            LayoutInflater inflater = LayoutInflater.from(context);
            created = inflater.inflate(tabType.viewResId, null);
        }
        else
        {
            THResideMenuItemImpl resideMenuItem = new THResideMenuItemImpl(context, tabType.drawableResId, tabType.stringResId);
            resideMenuItem.setIcon(tabType.drawableResId);
            resideMenuItem.setTitle(tabType.stringResId);
            created = resideMenuItem;
        }
        created.setTag(tabType);

        //Add the background selector
        created.setBackgroundResource(R.drawable.basic_transparent_selector);

        return created;
    }
}
