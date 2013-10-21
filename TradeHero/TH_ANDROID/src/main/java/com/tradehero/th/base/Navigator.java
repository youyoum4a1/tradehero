package com.tradehero.th.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.BaseFragment;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/** Created with IntelliJ IDEA. User: tho Date: 9/30/13 Time: 5:59 PM Copyright (c) TradeHero */
public class Navigator
{
    private static final String TAG = Navigator.class.getSimpleName();

    private final Context context;
    private final FragmentFactory fragmentFactory;
    private final int[] animation;
    protected final FragmentManager manager;

    private int fragmentContentId;
    private boolean animationInitiated;

    //<editor-fold desc="Constructors">
    public Navigator(Context context, FragmentManager manager, int fragmentContentId)
    {
        this.animation = new int[4];
        this.context = context;
        this.manager = manager;
        this.fragmentContentId = fragmentContentId;
        this.fragmentFactory = new FragmentFactory();
    }

    public Navigator(Activity activity, FragmentManager manager)
    {
        this(activity, manager, 0);
        setFragmentContentId(((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0).getId());
    }
    //</editor-fold>

    public void setAnimation(int enter, int exit, int popEnter, int popExit)
    {
        this.animation[0] = enter;
        this.animation[1] = exit;
        this.animation[2] = popEnter;
        this.animation[3] = popExit;
        this.animationInitiated = true;
    }
    private int[] getSafeAnimation()
    {
        if (animationInitiated) return this.animation;
        setAnimation(R.anim.slide_right_in, R.anim.slide_left_out,
                R.anim.slide_left_in, R.anim.slide_right_out);
        return animation;
    }

    public void setFragmentContentId(int fragmentContentId)
    {
        this.fragmentContentId = fragmentContentId;
    }

    protected Fragment getFragmentAtTopStack()
    {
        if (manager.getBackStackEntryCount() == 0)
        {
            return null;
        }
        String fragmentTag = manager.getBackStackEntryAt(manager.getBackStackEntryCount() - 1).getName();
        Fragment currentFragment = manager.findFragmentByTag(fragmentTag);
        return currentFragment;
    }

    public Fragment pushFragment(Class<? extends Fragment> fragmentClass, Bundle args, boolean withAnimation)
    {
        THLog.d(TAG, "Pushing fragment " + fragmentClass.getSimpleName());
        Fragment fragment = fragmentFactory.getInstance(fragmentClass, args);
        FragmentTransaction transaction = manager.beginTransaction();
        if (withAnimation)
        {
            int[] anim = getSafeAnimation();
            transaction.setCustomAnimations(anim[0], anim[1], anim[2], anim[3]);
        }
        transaction.replace(fragmentContentId, fragment)
                .addToBackStack(null)
                .commit();

        return fragment;
    }

    public void pushFragment(Class<? extends Fragment> fragmentClass)
    {
        pushFragment(fragmentClass, null);
    }

    public void pushFragment(Class<? extends Fragment> fragmentClass, Bundle args)
    {
        pushFragment(fragmentClass, args, true);
    }

    public void popFragment()
    {
        THLog.d(TAG, "Popping fragment, count: " + manager.getBackStackEntryCount());
        manager.popBackStack();
    }

    private class FragmentFactory
    {
        private Map<Class<?>, WeakReference<Fragment>> instances = new HashMap<>();

        public Fragment getInstance(Class<? extends Fragment> clss, Bundle args)
        {
            Fragment fragment = null;
            WeakReference<Fragment> weakFragment = instances.get(clss);
            if (weakFragment != null)
            {
                fragment = weakFragment.get();
            }

            if (fragment == null)
            {
                fragment = Fragment.instantiate(context, clss.getName(), args);
                instances.put(clss, new WeakReference<>(fragment));
            }
            // TODO I'm not sure this is a correct way to check whether the fragment is active
            else if (!fragment.isVisible())
            {
                fragment.setArguments(args);
            }
            else if (fragment instanceof BaseFragment.ArgumentsChangeListener)
            {
                ((BaseFragment.ArgumentsChangeListener) fragment).onArgumentsChanged(args);
            }
            else
            {
                THLog.d(TAG, "Args could not be passed to existing instance of " + fragment.getClass().getSimpleName());
            }
            return fragment;
        }
    }
}
