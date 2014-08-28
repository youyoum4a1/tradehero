package com.tradehero.th.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.TabHost;
import com.special.residemenu.ResideMenu;
import com.tradehero.th.R;
import com.tradehero.th.base.Navigator;
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
    private static final boolean TAB_SHOULD_ADD_TO_BACKSTACK = false;
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
            //currentDashboardFragment.onResume();
            return;
        }

        final String expectedTag = activity.getString(thIntent.getDashboardType().stringKeyResId);
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

    private <T extends Fragment> T goToTab(@NotNull RootFragmentType tabType, TabHost.OnTabChangeListener changeListener)
    {
        mOnTabChangedListener = changeListener;
        return goToTab(tabType);
    }

    public <T extends Fragment> T goToTab(@NotNull RootFragmentType tabType)
    {
        return goToTab(tabType, TAB_SHOULD_ADD_TO_BACKSTACK);
    }

    public <T extends Fragment> T goToTab(@NotNull RootFragmentType tabType, Boolean shouldAddToBackStack)
    {
        return goToTab(tabType, shouldAddToBackStack, TAB_SHOW_HOME_AS_UP);
    }

    public <T extends Fragment> T goToTab(@NotNull RootFragmentType tabType, Boolean shouldAddToBackStack, Boolean showHomeKeyAsUp)
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
        T tabFragment = pushFragment(targetFragmentClass, args, null, null, shouldAddToBackStack, showHomeKeyAsUp);

        updateTabBarOnTabChanged(((Object)tabFragment).getClass().getName());
        return tabFragment;
    }

    private void postPushActionFragment(final THIntent thIntent)
    {
        final Class<? extends Fragment> actionFragment = thIntent.getActionFragment();
        if (actionFragment == null)
        {
            return;
        }

        Fragment currentDashboardFragment = manager.findFragmentById(R.id.realtabcontent);
        currentDashboardFragment.getView().post(new Runnable()
        {
            // This is the way we found to make sure we do not superimpose 2 fragments.
            @Override public void run()
            {
                pushFragment(actionFragment, thIntent.getBundle());
            }
        });
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
