package com.tradehero.th.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.tradehero.th.activities.AuthenticationActivity;
import com.tradehero.th.fragments.authentication.SignInOrUpFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class DashboardNavigator extends Navigator<FragmentActivity>
{
    private static final boolean TAB_SHOW_HOME_AS_UP = false;

    private Set<DashboardFragmentWatcher> dashboardFragmentWatchers = new HashSet<>();

    public DashboardNavigator(FragmentActivity fragmentActivity, int fragmentContentId)
    {
        this(fragmentActivity, fragmentContentId, null, 1);
    }

    public DashboardNavigator(FragmentActivity fragmentActivity, int fragmentContentId, Class<? extends Fragment> initialFragment,
            int minimumBackstackSize)
    {
        super(fragmentActivity, fragmentActivity.getSupportFragmentManager(), fragmentContentId, minimumBackstackSize);

        if (initialFragment != null)
        {
            Fragment fragment = Fragment.instantiate(activity, initialFragment.getName(), new Bundle());
            FragmentTransaction transaction = manager.beginTransaction();
            transaction
                    .replace(fragmentContentId, fragment)
                    .commit();
        }
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
            @SuppressWarnings("unchecked")
            T castedFragment = (T) getCurrentFragment();
            return castedFragment;
        }

        Bundle args = new Bundle();

        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        manager.executePendingTransactions();

        @SuppressWarnings("unchecked")
        Class<T> targetFragmentClass = (Class<T>) tabType.fragmentClass;
        T tabFragment = pushFragment(targetFragmentClass, args, null, null, showHomeKeyAsUp);

        resetBackPressCount();
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
