package com.tradehero.th.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TabHost;
import com.special.ResideMenu.ResideMenu;
import com.tradehero.th.R;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import com.tradehero.th.models.intent.THIntent;
import com.tradehero.th.utils.DaggerUtils;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import timber.log.Timber;

public class DashboardNavigator extends Navigator
{
    private final FragmentActivity activity;

    private static final String BUNDLE_KEY = "key";
    private Set<TabHost.OnTabChangeListener> mOnTabChangedListeners;
    private TabHost.OnTabChangeListener mOnTabChangedListener;
    private Animation slideInAnimation;
    private Animation slideOutAnimation;

    @Inject ResideMenu resideMenu;

    public DashboardNavigator(Context context, FragmentManager manager, int fragmentContentId)
    {
        super(context, manager, fragmentContentId);
        this.activity = (FragmentActivity) context;
        initAnimation();
        DaggerUtils.inject(this);
    }

    @Deprecated
    public void addOnTabChangeListener(TabHost.OnTabChangeListener onTabChangeListener)
    {
        if (mOnTabChangedListeners == null)
        {
            mOnTabChangedListeners = new HashSet<TabHost.OnTabChangeListener>();
        }
        mOnTabChangedListeners.add(onTabChangeListener);
    }

    @Deprecated
    public void removeOnTabChangeListener(TabHost.OnTabChangeListener onTabChangeListener)
    {
        if (mOnTabChangedListeners == null)
        {
            return;
        }
        mOnTabChangedListeners.remove(onTabChangeListener);
    }

    private void initAnimation()
    {
        slideInAnimation = AnimationUtils.loadAnimation(activity, R.anim.slide_in);
        slideOutAnimation = AnimationUtils.loadAnimation(activity, R.anim.slide_out);
    }

    /**
     * To be called when we want it to be GC'ed
     */
    public void onDestroy()
    {
        if (mOnTabChangedListeners != null)
        {
            mOnTabChangedListeners.clear();
            mOnTabChangedListeners = null;
        }

        slideInAnimation.setAnimationListener(null);
        slideOutAnimation.setAnimationListener(null);
        slideInAnimation = null;
        slideOutAnimation = null;
    }

    public String makeFragmentName(DashboardTabType tabType)
    {
        return "TH-tab:"+tabType.ordinal();
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
                bundle.putString(BUNDLE_KEY, activity.getString(targetTabType.stringKeyResId));
                targetFragment =
                        Fragment.instantiate(activity, targetTabType.fragmentClass.getName(), bundle);
                ft.add(R.id.main_fragment, targetFragment, name);
                Timber.d("replaceTab add targetFragment %s",targetFragment);
            }
            ft.commitAllowingStateLoss();

        } else {
            //resideMenu.clearIgnoredViewList();

            Timber.d("replaceTab replace findFragmentById %s",manager.findFragmentById(R.id.main_fragment));
            Bundle bundle = new Bundle();
            bundle.putString(BUNDLE_KEY, activity.getString(targetTabType.stringKeyResId));
            Fragment targetFragment =
                    Fragment.instantiate(activity, targetTabType.fragmentClass.getName(), bundle);
            manager
                    .beginTransaction()
                    .replace(R.id.main_fragment, targetFragment, "fragment")
                    //.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commitAllowingStateLoss();

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

    public void goToTab(DashboardTabType tabType, TabHost.OnTabChangeListener changeListener)
    {
        Timber.d("goToTab %s with listener %s", tabType, changeListener);
        mOnTabChangedListener = changeListener;
        goToTab(tabType);
    }

    public void goToTab(DashboardTabType tabType)
    {
        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        manager.executePendingTransactions();
        Fragment fragment = Fragment.instantiate(context, tabType.fragmentClass.getName(), new Bundle());
        fragment.setArguments(new Bundle());
        FragmentTransaction transaction = manager.beginTransaction();
        FragmentTransaction ft = transaction.replace(fragmentContentId, fragment);
        String backStackName = tabType.fragmentClass.getName();
        ft.addToBackStack(backStackName);
        ft.commitAllowingStateLoss();
    }

    //public void clearBackStack()
    //{
    //    int rootFragment = manager.getBackStackEntryAt(0).getId();
    //    manager.popBackStack(rootFragment, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    //}

    @Override public <T extends Fragment> T pushFragment(Class<T> fragmentClass, Bundle args)
    {
        resideMenu.closeMenu();
        T fragment = super.pushFragment(fragmentClass, args);
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

    private void notifyOnTabChanged(String tabId)
    {
        Timber.d("tabBarChanged to %s, backstack %d", tabId, manager.getBackStackEntryCount());
        if (mOnTabChangedListeners != null && mOnTabChangedListeners.size() > 0)
        {
            for(TabHost.OnTabChangeListener o:mOnTabChangedListeners)
            {
                if(o != null)
                {
                    o.onTabChanged(tabId);
                }
            }
        }
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
