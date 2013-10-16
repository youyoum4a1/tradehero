package com.tradehero.th.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.r11.app.FragmentTabHost;
import android.view.View;
import android.view.animation.AnimationUtils;
import com.tradehero.th.R;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.portfolio.PortfolioListFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardFragment;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.trending.TrendingFragment;

/** Created with IntelliJ IDEA. User: tho Date: 10/11/13 Time: 4:24 PM Copyright (c) TradeHero */
public class DashboardNavigator extends Navigator
{
    private final FragmentActivity activity;

    private static final String BUNDLE_KEY = "key";
    private FragmentTabHost mTabHost;

    public DashboardNavigator(Context context, FragmentManager manager, int fragmentContentId)
    {
        super(context, manager, fragmentContentId);
        this.activity = (FragmentActivity)context;
        initTabs();
    }

    private void initTabs()
    {
        mTabHost = (FragmentTabHost) activity.findViewById(android.R.id.tabhost);
        mTabHost.setup(activity, activity.getSupportFragmentManager(), R.id.realtabcontent);

        addNewTab(activity.getString(R.string.trending), R.drawable.trending_selector, TrendingFragment.class);
        addNewTab(activity.getString(R.string.community), R.drawable.community_selector, LeaderboardFragment.class);
        addNewTab(activity.getString(R.string.home), R.drawable.home_selector, MeTimelineFragment.class);
        addNewTab(activity.getString(R.string.portfolio), R.drawable.portfolio_selector, PortfolioListFragment.class);
        addNewTab(activity.getString(R.string.store), R.drawable.store_selector, StoreScreenFragment.class);

        mTabHost.setCurrentTabByTag(activity.getString(R.string.home));
    }

    private void addNewTab(String tabTag, int tabIndicatorDrawableId, Class<?> fragmentClass)
    {
        Bundle b = new Bundle();
        b.putString(BUNDLE_KEY, tabTag);
        mTabHost.addTab(mTabHost
                .newTabSpec(tabTag)
                .setIndicator("", activity.getResources().getDrawable(tabIndicatorDrawableId)),
                fragmentClass, b);
    }

    @Override public void pushFragment(Class<? extends Fragment> fragmentClass, Bundle args, boolean withAnimation)
    {
        super.pushFragment(fragmentClass, args, withAnimation);
        manager.executePendingTransactions();
        updateTabBarOnNavigate();
    }

    @Override public void popFragment()
    {
        super.popFragment();
        manager.executePendingTransactions();
        updateTabBarOnNavigate();
    }

    private void updateTabBarOnNavigate()
    {
        boolean shouldHideTabBar = manager.getBackStackEntryCount() >= 1;

        if (shouldHideTabBar)
        {
            hideTabBar();
        }
        else
        {
            showTabBar();
        }
    }

    private void showTabBar()
    {
        mTabHost.getTabWidget().setVisibility(View.VISIBLE);
        mTabHost.getTabWidget()
                .startAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_in));
    }

    private void hideTabBar()
    {
        mTabHost.getTabWidget()
                .startAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_out));
        mTabHost.getTabWidget().setVisibility(View.GONE);
    }
}
