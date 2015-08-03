package com.tradehero.th.fragments.base;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.AbsListView;
import com.etiennelawlor.quickreturn.library.views.NotifyingScrollView;
import com.tradehero.th.BottomTabs;
import com.tradehero.th.BottomTabsQuickReturnListViewListener;
import com.tradehero.th.BottomTabsQuickReturnRecyclerViewListener;
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
    @NonNull private final RecyclerView.OnScrollListener dashboardBottomTabRecyclerViewScrollListener;
    @NonNull public final DashboardTabHost dashboardTabHost;

    @Inject public DashboardFragmentOuterElements(
            @NonNull ActionBarDrawerToggle mDrawerToggle,
            @NonNull @BottomTabsQuickReturnListViewListener AbsListView.OnScrollListener dashboardBottomTabsListViewScrollListener,
            @NonNull @BottomTabsQuickReturnScrollViewListener NotifyingScrollView.OnScrollChangedListener dashboardBottomTabScrollViewScrollListener,
            @NonNull @BottomTabsQuickReturnRecyclerViewListener RecyclerView.OnScrollListener dashboardBottomTabRecyclerViewScrollListener,
            @NonNull @BottomTabs DashboardTabHost dashboardTabHost)
    {
        this.mDrawerToggle = mDrawerToggle;
        this.dashboardBottomTabsListViewScrollListener = dashboardBottomTabsListViewScrollListener;
        this.dashboardBottomTabScrollViewScrollListener = dashboardBottomTabScrollViewScrollListener;
        this.dashboardBottomTabRecyclerViewScrollListener = dashboardBottomTabRecyclerViewScrollListener;
        //noinspection ConstantConditions
        if (dashboardTabHost == null)
        {
            throw new NullPointerException("You cannot give a null DashboardTabHost");
        }
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

    @NonNull @Override public RecyclerView.OnScrollListener getRecyclerViewScrollListener()
    {
        return dashboardBottomTabRecyclerViewScrollListener;
    }

    @NonNull @Override public MovableBottom getMovableBottom()
    {
        //noinspection ConstantConditions
        if (dashboardTabHost == null)
        {
            throw new NullPointerException("DashboardTabHost was null");
        }
        return dashboardTabHost;
    }

    @Override public boolean onOptionItemsSelected(MenuItem item)
    {
        return mDrawerToggle.onOptionsItemSelected(item);
    }
}
