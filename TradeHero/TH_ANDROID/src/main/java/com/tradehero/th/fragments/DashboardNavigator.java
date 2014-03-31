package com.tradehero.th.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.r11.app.FragmentTabHost;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TabHost;
import com.tradehero.th.R;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import com.tradehero.th.models.intent.THIntent;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: tho Date: 10/11/13 Time: 4:24 PM Copyright (c) TradeHero */
public class DashboardNavigator extends Navigator
{
    private static final boolean ENABLE_TABBAR_ANIMATION = false;
    private final FragmentActivity activity;

    private static final String BUNDLE_KEY = "key";
    private FragmentTabHost mTabHost;
    private TabHost.OnTabChangeListener mOnTabChangedListener;
    private Animation slideInAnimation;
    private Animation slideOutAnimation;
    private View tabBarView;

    public DashboardNavigator(Context context, FragmentManager manager, int fragmentContentId)
    {
        super(context, manager, fragmentContentId);
        this.activity = (FragmentActivity) context;

        //initTabs();
        //initAnimation();
    }

    private void initAnimation()
    {
        slideInAnimation = AnimationUtils.loadAnimation(activity, R.anim.slide_in);
        slideOutAnimation = AnimationUtils.loadAnimation(activity, R.anim.slide_out);

        Animation.AnimationListener animationListener = new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation)
            {
                if (mTabHost != null)
                {
                    mTabHost.getTabWidget().setEnabled(false);
                }
            }

            @Override public void onAnimationEnd(Animation animation)
            {
                if (mTabHost != null)
                {
                    mTabHost.getTabWidget().setEnabled(true);
                }
            }

            @Override public void onAnimationRepeat(Animation animation)
            {

            }
        };
        slideInAnimation.setAnimationListener(animationListener);
        slideOutAnimation.setAnimationListener(animationListener);
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

        // Hack to fix the issue with typing inside the competition webview
        for (int i=0; i<DashboardTabType.values().length; ++i)
        {
            mTabHost.getTabWidget().getChildAt(i).setFocusable(false);
        }

        mTabHost.getTabWidget().setDividerDrawable(null);
        mTabHost.setCurrentTabByTag(activity.getString(R.string.dashboard_trending));

        tabBarView = mTabHost.findViewById(android.R.id.tabhost);

        if (!ENABLE_TABBAR_ANIMATION)
        {
            mTabHost.setVisibility(View.GONE);
        }
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

        tabBarView = null;

        slideInAnimation.setAnimationListener(null);
        slideOutAnimation.setAnimationListener(null);
        slideInAnimation = null;
        slideOutAnimation = null;
    }

    private TabHost.TabSpec makeTabSpec(DashboardTabType tabType)
    {
        return mTabHost.newTabSpec(activity.getString(tabType.stringResId))
                .setIndicator(null, activity.getResources().getDrawable(tabType.drawableResId));
    }

    private TabHost.TabSpec makeNewTabSpec(DashboardTabType tabType)
    {
        View tabView = activity.getLayoutInflater().inflate(tabType.viewResId, mTabHost.getTabWidget(), false);
        ImageView imageView = (ImageView) tabView.findViewById(android.R.id.icon);
        imageView.setImageResource(tabType.drawableResId);
        imageView.setVisibility(View.VISIBLE);

        return mTabHost.newTabSpec(activity.getString(tabType.stringResId))
                .setIndicator(tabView);
    }

    private void addNewTab(DashboardTabType tabType)
    {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY, activity.getString(tabType.stringResId));

        mTabHost.addTab(makeNewTabSpec(tabType), tabType.fragmentClass, bundle);
    }

    public String makeFragmentName(DashboardTabType tabType)
    {
        return "TH-tab:"+tabType.ordinal();
    }

    @Override
    public Fragment pushFragment(Class<? extends Fragment> fragmentClass, Bundle args, int[] anim,
            String backStackName)
    {
        return super.pushFragment(fragmentClass, args, anim, backStackName);
    }

    /**
     * Yes ,a better way is to use FragmentTabHost to manage the fragments and their states.
     * @param currentTab
     * @param targetTabType
     */
    public void replaceTab(DashboardTabType currentTab, DashboardTabType targetTabType)
    {
        if (false) {
            FragmentTransaction ft = manager.beginTransaction();
            String name = makeFragmentName(targetTabType);
            Fragment targetFragment = manager.findFragmentByTag(name);
            Fragment currentragment = null;
            if (currentTab != null) {
                currentragment = manager.findFragmentByTag(makeFragmentName(currentTab));
            }
            if (currentragment != null)
            {
                ft.detach(currentragment);
                Timber.d("replaceTab detach currentragment %s",currentragment);
            }
            if (targetFragment != null)
            {
                ft.attach(targetFragment);
                Timber.d("replaceTab attach targetFragment %s",targetFragment);
            }
            else
            {
                Bundle bundle = new Bundle();
                bundle.putString(BUNDLE_KEY, activity.getString(targetTabType.stringResId));
                targetFragment =
                        Fragment.instantiate(activity, targetTabType.fragmentClass.getName(), bundle);
                ft.add(R.id.main_fragment, targetFragment, name);
                Timber.d("replaceTab add targetFragment %s",targetFragment);
            }
            ft.commit();

        } else {
            //resideMenu.clearIgnoredViewList();

            Timber.d("replaceTab replace findFragmentById %s",manager.findFragmentById(R.id.main_fragment));
            Bundle bundle = new Bundle();
            bundle.putString(BUNDLE_KEY, activity.getString(targetTabType.stringResId));
            Fragment targetFragment =
                    Fragment.instantiate(activity, targetTabType.fragmentClass.getName(), bundle);
            manager
                    .beginTransaction()
                    .replace(R.id.main_fragment, targetFragment, "fragment")
                    //.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();

            Timber.d("replaceTab replace targetFragment %s,findFragmentById:%s",targetFragment,manager.findFragmentById(R.id.main_fragment));
        }



    }


    public void goToPage(final THIntent thIntent)
    {
        if (thIntent == null)
        {
            return;
        }

        if (thIntent.getDashboardType() == null)
        {
            Fragment currentDashboardFragment = manager.findFragmentById(R.id.realtabcontent);
            currentDashboardFragment.getArguments().putBundle(BasePurchaseManagerFragment.BUNDLE_KEY_THINTENT_BUNDLE, thIntent.getBundle());
            currentDashboardFragment.onResume();
            return;
        }

        final String expectedTag = activity.getString(thIntent.getDashboardType().stringResId);
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
        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        manager.executePendingTransactions();
        goToTab(tabType, null);
    }

    public void goToTab(DashboardTabType tabType, TabHost.OnTabChangeListener changeListener)
    {
        Timber.d("goToTab %s with listener %s", tabType, changeListener);
        if (mTabHost != null)
        {
            mOnTabChangedListener = changeListener;
            mTabHost.setCurrentTabByTag(activity.getString(tabType.stringResId));
        }
        showTabBar();
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
        Timber.d("BackStack count %d", manager.getBackStackEntryCount());
    }

    private void updateTabBarOnNavigate(Fragment currentFragment)
    {
        if (ENABLE_TABBAR_ANIMATION)
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
    }

    private void updateTabBarOnTabChanged(String tabId)
    {
        Timber.d("tabBarChanged to %s, backstack %d", tabId, manager.getBackStackEntryCount());

        if (mOnTabChangedListener != null)
        {
            Timber.d("Called further onTabChangedListener");
            mOnTabChangedListener.onTabChanged(tabId);
        }
        mOnTabChangedListener = null;
    }

    private void showTabBar()
    {
        if (ENABLE_TABBAR_ANIMATION)
        {
            if (tabBarView != null && tabBarView.getVisibility() != View.VISIBLE)
            {
                tabBarView.setVisibility(View.VISIBLE);
                tabBarView.startAnimation(slideInAnimation);
            }
        }
    }

    public void hideTabBar()//let fragment can control it
    {
        if (tabBarView != null && tabBarView.getVisibility() != View.GONE)
        {
            tabBarView.startAnimation(slideOutAnimation);
            tabBarView.setVisibility(View.GONE);
        }
    }
}
