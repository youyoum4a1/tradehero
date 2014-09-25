package com.tradehero.th.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.TabHost;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import com.tradehero.th.models.intent.THIntent;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class DashboardNavigator extends Navigator<FragmentActivity>
{
    public static final String BUNDLE_KEY_RETURN_FRAGMENT = Navigator.class.getName() + ".returnFragment";

    private static final boolean TAB_SHOW_HOME_AS_UP = false;

    private Set<DashboardFragmentWatcher> dashboardFragmentWatchers = new HashSet<>();

    private TabHost.OnTabChangeListener mOnTabChangedListener;

    public DashboardNavigator(FragmentActivity context, FragmentManager manager, int fragmentContentId)
    {
        super(context, manager, fragmentContentId);
    }

    /**
     * To be called when we want it to be GC'ed
     */
    public void onDestroy()
    {
    }

    public <T extends Fragment> T goToTab(@NotNull RootFragmentType tabType)
    {
        return goToTab(tabType, TAB_SHOW_HOME_AS_UP);
    }

    public <T extends Fragment> T goToTab(@NotNull RootFragmentType tabType, boolean showHomeKeyAsUp)
    {
        if (tabType.fragmentClass.isInstance(getCurrentFragment()))
        {
            return (T) getCurrentFragment();
        }

        Bundle args = new Bundle();

        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        manager.executePendingTransactions();

        @SuppressWarnings("unchecked")
        Class<T> targetFragmentClass = (Class<T>) tabType.fragmentClass;
        T tabFragment = pushFragment(targetFragmentClass, args, null, null, showHomeKeyAsUp);

        updateTabBarOnTabChanged(((Object)tabFragment).getClass().getName());
        return tabFragment;
    }

    @Override public <T extends Fragment> T pushFragment(@NotNull Class<T> fragmentClass, Bundle args, @Nullable int[] anim,
            @Nullable String backStackName, boolean showHomeAsUp)
    {
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment instanceof DashboardFragment)
        {
            DashboardFragment currentDashboardFragment = (DashboardFragment) currentFragment;
            if (!currentDashboardFragment.allowNavigateTo(fragmentClass, args))
            {
                return null;
            }
        }

        T fragment = super.pushFragment(fragmentClass, args, anim, backStackName, showHomeAsUp);
        executePending();

        onFragmentChanged(activity, fragmentClass, args);
        return fragment;
    }

    private void executePending()
    {
        manager.executePendingTransactions();
    }

    @Override public void popFragment(String backStackName)
    {
        super.popFragment(backStackName);

        if (!isBackStackEmpty())
        {
            executePending();
        }
        onFragmentChanged(activity, getCurrentFragment().getClass(), null);
        Timber.d("BackStack count %d", manager.getBackStackEntryCount());
    }


    public void popFragment()
    {
        Fragment currentDashboardFragment = manager.findFragmentById(R.id.realtabcontent);

        String backStackName = null;
        if (currentDashboardFragment != null && currentDashboardFragment.getArguments() != null)
        {
            Bundle args = currentDashboardFragment.getArguments();
            backStackName = args.getString(BUNDLE_KEY_RETURN_FRAGMENT);
        }
        popFragment(backStackName);
    }

    private void updateTabBarOnTabChanged(String tabId)
    {
        Timber.d("tabBarChanged to %s, backstack %d", tabId, manager.getBackStackEntryCount());
        resetBackPressCount();

        if (mOnTabChangedListener != null)
        {
            Timber.d("Called further onTabChangedListener");
            mOnTabChangedListener.onTabChanged(tabId);
        }
        mOnTabChangedListener = null;
    }

    //<editor-fold desc="DashboardFragmentWatcher">
    private <T extends Fragment> void onFragmentChanged(FragmentActivity activity, Class<T> fragmentClass, Bundle args)
    {
        for (DashboardFragmentWatcher dashboardFragmentWatcher: dashboardFragmentWatchers)
        {
            dashboardFragmentWatcher.onFragmentChanged(activity, fragmentClass, args);
        }
    }

    public void addDashboardFragmentWatcher(DashboardFragmentWatcher watcher)
    {
        dashboardFragmentWatchers.add(watcher);
    }

    public void clearDashboardFragmentWatchers()
    {
        dashboardFragmentWatchers.clear();
    }

    public void removeDashboardFragmentWatchers(DashboardFragmentWatcher watcher)
    {
        dashboardFragmentWatchers.remove(watcher);
    }

    public static interface DashboardFragmentWatcher
    {
        <T extends Fragment> void onFragmentChanged(FragmentActivity fragmentActivity, Class<T> fragmentClass, Bundle bundle);
    }
    //</editor-fold>
}
