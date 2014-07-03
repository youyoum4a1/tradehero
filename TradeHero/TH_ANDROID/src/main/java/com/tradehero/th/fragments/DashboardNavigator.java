package com.tradehero.th.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.TabHost;
import com.special.ResideMenu.ResideMenu;
import com.tradehero.th.R;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import com.tradehero.th.models.intent.THIntent;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class DashboardNavigator extends Navigator
{
    private static final boolean TAB_SHOULD_ADD_TO_BACKSTACK = false;
    private static final boolean TAB_SHOW_HOME_AS_UP = false;

    private TabHost.OnTabChangeListener mOnTabChangedListener;

    @Inject ResideMenu resideMenu;

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

        final String expectedTag = context.getString(thIntent.getDashboardType().stringKeyResId);
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

    private void goToTab(@NotNull DashboardTabType tabType, TabHost.OnTabChangeListener changeListener)
    {
        mOnTabChangedListener = changeListener;
        goToTab(tabType);
    }

    public void goToTab(@NotNull DashboardTabType tabType)
    {
        this.goToTab(tabType, TAB_SHOULD_ADD_TO_BACKSTACK);
    }

    public void goToTab(@NotNull DashboardTabType tabType, Boolean shouldAddToBackStack)
    {
        this.goToTab(tabType, shouldAddToBackStack, TAB_SHOW_HOME_AS_UP);
    }

    public void goToTab(@NotNull DashboardTabType tabType, Boolean shouldAddToBackStack, Boolean showHomeKeyAsUp)
    {
        Bundle args = new Bundle();

        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        manager.executePendingTransactions();

        updateTabBarOnTabChanged(pushFragment(tabType.fragmentClass, args, null, null, shouldAddToBackStack, showHomeKeyAsUp).getClass().getName());
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
        resideMenu.closeMenu();
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
