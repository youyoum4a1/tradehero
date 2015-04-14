package com.tradehero.th.fragments.base;

import android.support.annotation.NonNull;
import android.widget.AbsListView;
import com.etiennelawlor.quickreturn.library.views.NotifyingScrollView;
import com.special.residemenu.ResideMenu;
import com.tradehero.th.BottomTabs;
import com.tradehero.th.BottomTabsQuickReturnListViewListener;
import com.tradehero.th.BottomTabsQuickReturnScrollViewListener;
import com.tradehero.th.fragments.DashboardTabHost;
import com.tradehero.th.fragments.MovableBottom;
import javax.inject.Inject;

public class DashboardFragmentOuterElements implements FragmentOuterElements
{
    @NonNull public final ResideMenu resideMenu;
    @NonNull public final AbsListView.OnScrollListener dashboardBottomTabsListViewScrollListener;
    @NonNull public final NotifyingScrollView.OnScrollChangedListener
            dashboardBottomTabScrollViewScrollListener;
    @NonNull public final DashboardTabHost dashboardTabHost;

    @Inject public DashboardFragmentOuterElements(
            @NonNull ResideMenu resideMenu,
            @NonNull @BottomTabsQuickReturnListViewListener AbsListView.OnScrollListener dashboardBottomTabsListViewScrollListener,
            @NonNull @BottomTabsQuickReturnScrollViewListener NotifyingScrollView.OnScrollChangedListener dashboardBottomTabScrollViewScrollListener,
            @NonNull @BottomTabs DashboardTabHost dashboardTabHost)
    {
        this.resideMenu = resideMenu;
        this.dashboardBottomTabsListViewScrollListener = dashboardBottomTabsListViewScrollListener;
        this.dashboardBottomTabScrollViewScrollListener = dashboardBottomTabScrollViewScrollListener;
        this.dashboardTabHost = dashboardTabHost;
    }

    @Override public void openMenu()
    {
        resideMenu.openMenu();
    }

    @NonNull @Override public AbsListView.OnScrollListener getListViewScrollListener()
    {
        return dashboardBottomTabsListViewScrollListener;
    }

    @NonNull @Override public NotifyingScrollView.OnScrollChangedListener getScrollViewListener()
    {
        return dashboardBottomTabScrollViewScrollListener;
    }

    @NonNull @Override public MovableBottom getMovableBottom()
    {
        return dashboardTabHost;
    }
}
