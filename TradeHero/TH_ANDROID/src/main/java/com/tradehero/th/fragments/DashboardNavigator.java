package com.tradehero.th.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.TabHost;

import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.utils.DaggerUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import timber.log.Timber;

public class DashboardNavigator extends Navigator
{
    private static final boolean TAB_SHOULD_ADD_TO_BACKSTACK = false;
    private static final boolean TAB_SHOW_HOME_AS_UP = false;

    private TabHost.OnTabChangeListener mOnTabChangedListener;

    public DashboardNavigator(Context context, FragmentManager manager, int fragmentContentId)
    {
        super(context, manager, fragmentContentId);
        DaggerUtils.inject(this);
    }

    /**
     * To be called when we want it to be GC'ed
     */
    public void onDestroy()
    {
    }

    public void goToFragment(Class fragment,Bundle args)
    {
        this.goToFragment(fragment, args, TAB_SHOULD_ADD_TO_BACKSTACK, TAB_SHOW_HOME_AS_UP);
    }

    public void goToFragment(Class fragment, Bundle args,Boolean shouldAddToBackStack, Boolean showHomeKeyAsUp)
    {

        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        manager.executePendingTransactions();

        updateTabBarOnTabChanged(((Object) pushFragment(fragment, args, null, null, shouldAddToBackStack, showHomeKeyAsUp)).getClass().getName());
    }

    @Override public <T extends Fragment> T pushFragment(@NotNull Class<T> fragmentClass, Bundle args, @Nullable int[] anim,
            @Nullable String backStackName, Boolean shouldAddToBackStack, Boolean showHomeAsUp)
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

        T fragment = super.pushFragment(fragmentClass, args, anim, backStackName, shouldAddToBackStack, showHomeAsUp);
        executePending(fragment);
        return fragment;
    }

    private void executePending(Fragment fragment)
    {
        manager.executePendingTransactions();
    }

    @Override public void popFragment(String backStackName)
    {
        super.popFragment(backStackName);

        if (!isBackStackEmpty())
        {
            executePending(null);
        }
        Timber.d("BackStack count %d", manager.getBackStackEntryCount());
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
}
