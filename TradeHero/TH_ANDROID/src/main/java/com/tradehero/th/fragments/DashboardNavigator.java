package com.tradehero.th.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.r11.app.FragmentTabHost;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TabHost;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.dashboard.DashboardTabType;

/** Created with IntelliJ IDEA. User: tho Date: 10/11/13 Time: 4:24 PM Copyright (c) TradeHero */
public class DashboardNavigator extends Navigator
{
    public final static String TAG = DashboardNavigator.class.getSimpleName();
    private final FragmentActivity activity;

    private static final String BUNDLE_KEY = "key";
    private FragmentTabHost mTabHost;

    public DashboardNavigator(Context context, FragmentManager manager, int fragmentContentId, boolean withTab)
    {
        super(context, manager, fragmentContentId);
        this.activity = (FragmentActivity) context;

        if (withTab)
        {
            initTabs();
        }
    }

    private void initTabs()
    {
        mTabHost = (FragmentTabHost) activity.findViewById(android.R.id.tabhost);
        mTabHost.setup(activity, activity.getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener()
        {
            @Override public void onTabChanged(String tabId)
            {
                updateTabBarOnTabChanged(tabId);
            }
        });

        for (DashboardTabType tabType : DashboardTabType.values())
        {
            addNewTab(tabType);
        }

        mTabHost.setCurrentTabByTag(activity.getString(R.string.home));
    }

    /**
     * To be called when we want it to be GC'ed
     */
    public void onDestroy()
    {
        if (mTabHost != null)
        {
            mTabHost.setOnTabChangedListener(null);
        }
        mTabHost = null;
    }

    private TabHost.TabSpec makeTabSpec(DashboardTabType tabType)
    {
        return mTabHost.newTabSpec(activity.getString(tabType.stringResId));
    }

    private void addNewTab(DashboardTabType tabType)
    {
        Bundle b = new Bundle();
        b.putString(BUNDLE_KEY, activity.getString(tabType.stringResId));
        mTabHost.addTab(
                makeTabSpec(tabType)
                        .setIndicator(
                                "",
                                activity.getResources().getDrawable(tabType.drawableResId)
                        ),
                tabType.fragmentClass,
                b);
    }

    public void goToTab(DashboardTabType tabType)
    {
        if (mTabHost != null)
        {
            mTabHost.setCurrentTabByTag(makeTabSpec(tabType).getTag());
        }
    }

    //public void clearBackStack()
    //{
    //    int rootFragment = manager.getBackStackEntryAt(0).getId();
    //    manager.popBackStack(rootFragment, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    //}

    @Override public Fragment pushFragment(Class<? extends Fragment> fragmentClass, Bundle args, boolean withAnimation)
    {
        Fragment fragment = super.pushFragment(fragmentClass, args, withAnimation);
        manager.executePendingTransactions();
        if (mTabHost != null)
        {
            updateTabBarOnNavigate(fragment);
        }
        return fragment;
    }

    @Override public void popFragment()
    {
        super.popFragment();

        if (!isBackStackEmpty())
        {
            manager.executePendingTransactions();
            if (mTabHost != null)
            {
                updateTabBarOnNavigate(null);
            }
        }
        THLog.d(TAG, "BackstackCount " + manager.getBackStackEntryCount());
    }

    private void updateTabBarOnNavigate(Fragment currentFragment)
    {
        boolean shouldHideTabBar = manager.getBackStackEntryCount() >= 1;
        if (currentFragment instanceof BaseFragment.TabBarVisibilityInformer)
        {
            shouldHideTabBar = !((BaseFragment.TabBarVisibilityInformer) currentFragment).isTabBarVisible();
        }

        if (shouldHideTabBar)
        {
            hideTabBar();
        }
        else
        {
            showTabBar();
        }
    }

    private void updateTabBarOnTabChanged(String tag)
    {
        THLog.d(TAG, "tabBarChanged to " + tag + ", backstack " + manager.getBackStackEntryCount());
    }

    private void showTabBar()
    {
        View tabBar = mTabHost.findViewById(android.R.id.tabhost);
        if (tabBar != null)
        {
            tabBar.setVisibility(View.VISIBLE);
            tabBar.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_in));
        }
    }

    private void hideTabBar()
    {
        View tabBar = mTabHost.findViewById(android.R.id.tabhost);
        if (tabBar != null)
        {
            tabBar.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_out));
            tabBar.setVisibility(View.GONE);
        }
    }
}
