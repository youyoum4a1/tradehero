package com.tradehero.th.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.tradehero.th.activities.BaseActivity;
import com.tradehero.th.activities.OnBoardActivity;
import com.tradehero.th.activities.OneFragmentActivity;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import java.util.LinkedHashSet;
import java.util.Set;
import timber.log.Timber;

public class DashboardNavigator extends Navigator<FragmentActivity>
{
    private static final boolean TAB_SHOW_HOME_AS_UP = false;

    private Set<DashboardFragmentWatcher> dashboardFragmentWatchers = new LinkedHashSet<>();

    //<editor-fold desc="Constructors">
    public DashboardNavigator(FragmentActivity fragmentActivity, int fragmentContentId)
    {
        this(fragmentActivity, fragmentContentId, null, 1);
    }

    public DashboardNavigator(FragmentActivity fragmentActivity, int fragmentContentId, Class<? extends Fragment> initialFragment,
            int minimumBackstackSize)
    {
        this(fragmentActivity, fragmentContentId, initialFragment, minimumBackstackSize, new Bundle());
    }
    public DashboardNavigator(FragmentActivity fragmentActivity, int fragmentContentId, Class<? extends Fragment> initialFragment,
            int minimumBackstackSize,
            Bundle initialArgs)
    {
        super(fragmentActivity, fragmentActivity.getSupportFragmentManager(), fragmentContentId, minimumBackstackSize);

        if (initialFragment != null)
        {
            Fragment fragment = Fragment.instantiate(activity, initialFragment.getName(), initialArgs);
            FragmentTransaction transaction = manager.beginTransaction();
            transaction
                    .replace(fragmentContentId, fragment)
                    .commitAllowingStateLoss();
        }
    }
    //</editor-fold>

    /**
     * To be called when we want it to be GC'ed
     */
    public void onDestroy()
    {
    }

    public <T extends Fragment> T goToTab(@NonNull RootFragmentType tabType)
    {
        return goToTab(tabType, TAB_SHOW_HOME_AS_UP);
    }

    public <T extends Fragment> T goToTab(@NonNull RootFragmentType tabType, boolean showHomeKeyAsUp)
    {
        if (tabType.fragmentClass == null)
        {
            throw new IllegalArgumentException("You should not call this with RootFragmentType." + tabType);
        }

        if (tabType.fragmentClass.isInstance(getCurrentFragment()))
        {
            @SuppressWarnings("unchecked")
            T castedFragment = (T) getCurrentFragment();
            return castedFragment;
        }

        Bundle args = new Bundle();

        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        try
        {
            manager.executePendingTransactions();
        } catch (java.lang.IllegalStateException e)
        {
            Timber.d("goToTab after popBackStack :" + e.toString());
        }

        @SuppressWarnings("unchecked")
        Class<T> targetFragmentClass = (Class<T>) tabType.fragmentClass;
        T tabFragment = pushFragment(targetFragmentClass, args, null, null, showHomeKeyAsUp);

        resetBackPressCount();
        return tabFragment;
    }

    @Override public <T extends Fragment> T pushFragment(@NonNull Class<T> fragmentClass, Bundle args, @Nullable int[] anim,
            @Nullable String backStackName, boolean showHomeAsUp)
    {
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment instanceof BaseFragment)
        {
            BaseFragment currentDashboardFragment = (BaseFragment) currentFragment;
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
        try
        {
            manager.executePendingTransactions();
        } catch (java.lang.IllegalStateException e)
        {
            Timber.d("executePending " + e.toString());
        }
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

    public boolean hasBackStackName(String backStackName)
    {
        int index = 0;
        while (index < manager.getBackStackEntryCount())
        {
            if (manager.getBackStackEntryAt(index).getName().equals(backStackName))
            {
                return true;
            }
            index++;
        }
        return false;
    }

    //<editor-fold desc="DashboardFragmentWatcher">
    private <T extends Fragment> void onFragmentChanged(FragmentActivity activity, Class<T> fragmentClass, Bundle args)
    {
        for (DashboardFragmentWatcher dashboardFragmentWatcher : dashboardFragmentWatchers)
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

    public interface DashboardFragmentWatcher
    {
        <T extends Fragment> void onFragmentChanged(FragmentActivity fragmentActivity, Class<T> fragmentClass, Bundle bundle);
    }
    //</editor-fold>

    public void launchTabActivity(@NonNull RootFragmentType tabType)
    {
        if (tabType.activityClass == null)
        {
            throw new IllegalArgumentException("You should not call this with RootFragmentType." + tabType);
        }
        launchActivity(tabType.activityClass);
    }

    public void launchActivity(@NonNull Class<? extends Activity> activityClass)
    {
        launchActivity(activityClass, null);
    }

    public void launchActivity(@NonNull Class<?extends Activity> activityClass, @Nullable Bundle extras)
    {
        Intent startIntent = new Intent(activity, activityClass);
        if (extras != null)
        {
            startIntent.putExtras(extras);
        }
        if (OneFragmentActivity.class.isAssignableFrom(activityClass)
                || OnBoardActivity.class.isAssignableFrom(activityClass))
        {
            activity.startActivityForResult(startIntent, BaseActivity.REQUEST_CODE_ROUTE);
        }
        else
        {
            activity.startActivity(startIntent);
        }
    }
}
