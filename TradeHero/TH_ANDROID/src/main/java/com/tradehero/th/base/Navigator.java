package com.tradehero.th.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.activities.ActivityHelper;
import com.tradehero.th.activities.SettingsActivity;
import com.tradehero.th.fragments.settings.SettingsFragment;

/** Created with IntelliJ IDEA. User: tho Date: 9/30/13 Time: 5:59 PM Copyright (c) TradeHero */
public class Navigator
{
    private static final String TAG = Navigator.class.getSimpleName();
    public static final String NAVIGATE_FRAGMENT_NO_CACHE = Navigator.class.getName();
    public static final int[] TUTORIAL_ANIMATION = new int[] {
            R.anim.card_flip_right_in, R.anim.card_flip_right_out,
            R.anim.card_flip_left_in, R.anim.card_flip_left_out};

    private final Context context;
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

    private Fragment pushFragment(Class<? extends Fragment> fragmentClass, Bundle args, int[] anim)
    {
        THLog.d(TAG, "Pushing fragment " + fragmentClass.getSimpleName());
        Fragment fragment = Fragment.instantiate(context, fragmentClass.getName(), args);
        fragment.setArguments(args);
        FragmentTransaction transaction = manager.beginTransaction();
        if (anim != null)
        {
            transaction.setCustomAnimations(anim[0], anim[1], anim[2], anim[3]);
        }
        transaction.replace(fragmentContentId, fragment)
                .addToBackStack(null)
                .commit();

        return fragment;
    }

    public Fragment pushFragment(Class<? extends Fragment> fragmentClass, Bundle args, boolean withAnimation)
    {
        return pushFragment(fragmentClass, args, getSafeAnimation());
    }

    public void showTutorial(Fragment ownerFragmentClass)
    {
        //if (ownerFragmentClass instanceof WithTutorial)
        //{
        //    int tutorialLayoutId = ((WithTutorial) ownerFragmentClass).getTutorialLayout();
        //    Bundle bundle = new Bundle();
        //    bundle.putInt(TutorialFragment.TUTORIAL_LAYOUT, tutorialLayoutId);
        //    THLog.d(TAG, "Showing tutorial for " + ownerFragmentClass.getClass().getName());
        //    pushFragment(TutorialFragment.class, bundle, TUTORIAL_ANIMATION);
        //}
        ActivityHelper.launchAuthentication(context);
    }

    public void openSettings()
    {
        //Intent intent = new Intent(context, SettingsActivity.class);
        //context.startActivity(intent);
        //((Activity)context).overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.nothing);
        pushFragment(SettingsFragment.class);
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

    public boolean isBackStackEmpty()
    {
        return manager.getBackStackEntryCount() == 0;
    }
}
