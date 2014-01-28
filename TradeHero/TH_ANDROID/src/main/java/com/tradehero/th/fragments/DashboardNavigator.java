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
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.base.Application;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import com.tradehero.th.fragments.security.WatchlistEditFragment;
import com.tradehero.th.fragments.timeline.TimelineFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.models.intent.THIntent;

/** Created with IntelliJ IDEA. User: tho Date: 10/11/13 Time: 4:24 PM Copyright (c) TradeHero */
public class DashboardNavigator extends Navigator
{
    public final static String TAG = DashboardNavigator.class.getSimpleName();
    private final FragmentActivity activity;

    private static final String BUNDLE_KEY = "key";
    private FragmentTabHost mTabHost;
    private TabHost.OnTabChangeListener mOnTabChangedListener;

    public DashboardNavigator(Context context, FragmentManager manager, int fragmentContentId)
    {
        super(context, manager, fragmentContentId);
        this.activity = (FragmentActivity) context;

        initTabs();
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
        mOnTabChangedListener = null;
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

    public void goToPage(final THIntent thIntent)
    {
        final String expectedTag = Application.getResourceString(thIntent.getDashboardType().stringResId);
        goToTab(
                thIntent.getDashboardType(),
                new TabHost.OnTabChangeListener()
                {
                    @Override public void onTabChanged(String tabTag)
                    {
                        if (expectedTag.equals(tabTag))
                        {
                            postPushActionFragment(thIntent);
                        }
                    }
                });
    }

    private void postPushActionFragment(final THIntent thIntent)
    {
        final Class<? extends Fragment> actionFragment = thIntent.getActionFragment();
        if (actionFragment == null)
        {
            return;
        }

        mTabHost.getCurrentTabView().post(new Runnable()
        {
            // This is the way we found to make sure we do not superimpose 2 fragments.
            @Override public void run()
            {
                pushFragment(actionFragment, thIntent.getBundle());
            }
        });
    }

    public void goToTab(DashboardTabType tabType)
    {
        goToTab(tabType, null);
    }

    public void goToTab(DashboardTabType tabType, TabHost.OnTabChangeListener changeListener)
    {
        THLog.d(TAG, "goToTab " + tabType + " with listener " + changeListener);
        if (mTabHost != null)
        {
            mOnTabChangedListener = changeListener;
            mTabHost.setCurrentTabByTag(makeTabSpec(tabType).getTag());
        }
    }

    public void openTimeline(int userId)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(TimelineFragment.BUNDLE_KEY_SHOW_USER_ID, userId);
        pushFragment(TimelineFragment.class, bundle);
    }

    //public void clearBackStack()
    //{
    //    int rootFragment = manager.getBackStackEntryAt(0).getId();
    //    manager.popBackStack(rootFragment, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    //}

    @Override public Fragment pushFragment(Class<? extends Fragment> fragmentClass, Bundle args)
    {
        Fragment fragment = super.pushFragment(fragmentClass, args);
        executePending(fragment);
        return fragment;
    }

    private void executePending(Fragment fragment)
    {
        manager.executePendingTransactions();
        if (mTabHost != null)
        {
            updateTabBarOnNavigate(fragment);
        }
    }

    @Override public void popFragment(String backStackName)
    {
        super.popFragment(backStackName);

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

    private void updateTabBarOnTabChanged(String tabId)
    {
        THLog.d(TAG, "tabBarChanged to " + tabId + ", backstack " + manager.getBackStackEntryCount());

        if (mOnTabChangedListener != null)
        {
            THLog.d(TAG, "Called further onTabChangedListener");
            mOnTabChangedListener.onTabChanged(tabId);
        }
        mOnTabChangedListener = null;
    }

    private void showTabBar()
    {
        View tabBar = mTabHost.findViewById(android.R.id.tabhost);
        if (tabBar != null && tabBar.getVisibility() != View.VISIBLE)
        {
            tabBar.setVisibility(View.VISIBLE);
            tabBar.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_in));
        }
    }

    private void hideTabBar()
    {
        View tabBar = mTabHost.findViewById(android.R.id.tabhost);
        if (tabBar != null && tabBar.getVisibility() != View.GONE)
        {
            tabBar.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_out));
            tabBar.setVisibility(View.GONE);
        }
    }

    public void openSecurityProfile(SecurityId securityId)
    {
        Bundle args = new Bundle();
        args.putBundle(BuySellFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
        pushFragment(BuySellFragment.class, args);
    }
}
