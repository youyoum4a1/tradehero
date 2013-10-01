package com.tradehero.th.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;
import com.tradehero.th.R;
import java.util.HashMap;
import java.util.Map;

/** Created with IntelliJ IDEA. User: tho Date: 9/30/13 Time: 5:59 PM Copyright (c) TradeHero */
public class Navigator
{
    private static final String TAG = Navigator.class.getName();
    private final FragmentActivity fragmentActivity;
    private final FragmentFactory fragmentFactory;
    private final int[] animation;
    private int fragmentContentId;
    private boolean animationInitiated;

    public Navigator(FragmentActivity fragmentActivity, int fragmentContentId)
    {
        this.animation = new int[4];
        this.fragmentActivity = fragmentActivity;
        this.fragmentContentId = fragmentContentId;
        this.fragmentFactory = new FragmentFactory();
    }

    public Navigator(FragmentActivity fragmentActivity)
    {
        this(fragmentActivity, 0);

        setFragmentContentId(((ViewGroup) fragmentActivity.findViewById(android.R.id.content)).getChildAt(0).getId());
    }

    public void setAnimation(int enter, int exit, int popEnter, int popExit)
    {
        this.animation[0] = enter;
        this.animation[1] = exit;
        this.animation[2] = popEnter;
        this.animation[3] = popExit;
        this.animationInitiated = true;
    }

    public void setFragmentContentId(int fragmentContentId)
    {
        this.fragmentContentId = fragmentContentId;
    }

    private int[] getSafeAnimation()
    {
        if (animationInitiated) return this.animation;
        setAnimation(R.anim.slide_right_in, R.anim.slide_left_out,
                R.anim.slide_left_in, R.anim.slide_right_out);
        return animation;
    }

    public void pushFragment(Class<? extends Fragment> fragmentClass, Bundle args, boolean withAnimation)
    {
        Fragment fragment = fragmentFactory.getInstance(fragmentClass, args);
        FragmentTransaction transaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
        if (withAnimation)
        {
            int[] anim = getSafeAnimation();
            transaction.setCustomAnimations(anim[0], anim[1], anim[2], anim[3]);
        }
        transaction.replace(fragmentContentId, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void pushFragment(Class<? extends Fragment> fragmentClass)
    {
        pushFragment(fragmentClass, null, true);
    }

    public void pushFragment(Class<? extends Fragment> fragmentClass, Bundle args)
    {
        pushFragment(fragmentClass, args, true);
    }


    private void popFragment()
    {
        fragmentActivity.getSupportFragmentManager().popBackStack();
    }

    private class FragmentFactory
    {
        private Map<Class<?>, Fragment> instances = new HashMap<>();

        public Fragment getInstance(Class<?> clss, Bundle args)
        {
            Fragment fragment = instances.get(clss);
            if (fragment == null)
            {
                fragment = Fragment.instantiate(fragmentActivity, clss.getName(), args);
                instances.put(clss, fragment);
            }
            else
            {
                fragment.setArguments(args);
            }
            return fragment;
        }
    }

    /*
    private void pushTrendingIn()
    {
        THLog.i(TAG, "pushTrendingIn");

    }

    private void pushSearchIn()
    {
        THLog.i(TAG, "pushSearchIn");
        Fragment searchFragment = fragmentFactory.getInstance();
    }

    private void pushTradeIn(SecurityCompactDTO securityCompactDTO)
    {
        THLog.i(TAG, "pushTradeIn");
        TradeFragment tradeFragment = (TradeFragment) fragmentFactory.getInstance(TradeFragment.class);
        fragmentActivity.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_right_in, R.anim.slide_left_out,
                        R.anim.slide_left_in, R.anim.slide_right_out
                )
                .replace(fragmentActivity.get, tradeFragment)
                        //.show(searchFragment)
                .addToBackStack(null)
                .commit();
        tradeFragment.display(securityCompactDTO);
    }
    */

}
