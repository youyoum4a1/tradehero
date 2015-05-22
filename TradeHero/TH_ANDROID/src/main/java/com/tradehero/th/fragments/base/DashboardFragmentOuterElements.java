package com.tradehero.th.fragments.base;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.widget.AbsListView;
import com.etiennelawlor.quickreturn.library.views.NotifyingScrollView;
import com.tradehero.th.BottomTabs;
import com.tradehero.th.BottomTabsQuickReturnListViewListener;
import com.tradehero.th.BottomTabsQuickReturnScrollViewListener;
import com.tradehero.th.fragments.DashboardTabHost;
import com.tradehero.th.fragments.MovableBottom;
import javax.inject.Inject;

public class DashboardFragmentOuterElements implements FragmentOuterElements
{
    @NonNull private final ActionBarDrawerToggle mDrawerToggle;
    @NonNull public final AbsListView.OnScrollListener dashboardBottomTabsListViewScrollListener;
    @NonNull public final NotifyingScrollView.OnScrollChangedListener
            dashboardBottomTabScrollViewScrollListener;
    @NonNull public final DashboardTabHost dashboardTabHost;

    @Inject public DashboardFragmentOuterElements(
            @NonNull ActionBarDrawerToggle mDrawerToggle,
            @NonNull @BottomTabsQuickReturnListViewListener AbsListView.OnScrollListener dashboardBottomTabsListViewScrollListener,
            @NonNull @BottomTabsQuickReturnScrollViewListener NotifyingScrollView.OnScrollChangedListener dashboardBottomTabScrollViewScrollListener,
            @NonNull @BottomTabs DashboardTabHost dashboardTabHost)
    {
        this.mDrawerToggle = mDrawerToggle;
        this.dashboardBottomTabsListViewScrollListener = dashboardBottomTabsListViewScrollListener;
        this.dashboardBottomTabScrollViewScrollListener = dashboardBottomTabScrollViewScrollListener;
        this.dashboardTabHost = dashboardTabHost;
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

    @Override public boolean onOptionItemsSelected(MenuItem item)
    {
        return mDrawerToggle.onOptionsItemSelected(item);
    }
}
