package com.tradehero.th.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.settings.DashboardPreferenceFragment;
import com.tradehero.th.utils.DeviceUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import timber.log.Timber;

class Navigator<ActivityType extends Activity>
{
    private static final boolean DEFAULT_ADD_TO_BACK_STACK = true;
    private static final boolean DEFAULT_SHOW_HOME_KEY_AS_UP = true;

    public static final int[] TUTORIAL_ANIMATION = new int[] {
            R.anim.card_flip_right_in, R.anim.card_flip_right_out,
            R.anim.card_flip_left_in, R.anim.card_flip_left_out
    };
    public static final int[] PUSH_UP_FROM_BOTTOM = new int[] {
            R.anim.slide_in_from_bottom, R.anim.slide_out_to_top,
            R.anim.slide_in_from_top, R.anim.slide_out_to_bottom
    };
    public static final int[] DEFAULT_FRAGMENT_ANIMATION = new int[] {
            R.anim.slide_right_in, R.anim.slide_left_out,
            R.anim.slide_left_in, R.anim.slide_right_out
    };

    protected final ActivityType activity;
    protected final FragmentManager manager;
    private int fragmentContentId;
    private int backPressedCount;

    //<editor-fold desc="Constructors">
    public Navigator(ActivityType activity, FragmentManager manager)
    {
        this(activity, manager, 0);
        setFragmentContentId(((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0).getId());
    }

    public Navigator(ActivityType activity, FragmentManager manager, int fragmentContentId)
    {
        this.activity = activity;
        this.manager = manager;
        this.fragmentContentId = fragmentContentId;
    }
    //</editor-fold>

    public void setFragmentContentId(int fragmentContentId)
    {
        this.fragmentContentId = fragmentContentId;
    }

    public <T extends Fragment> T pushFragment(@NotNull Class<T> fragmentClass)
    {
        return pushFragment(fragmentClass, new Bundle());
    }

    public <T extends Fragment> T pushFragment(@NotNull Class<T> fragmentClass, Bundle args)
    {
        return pushFragment(fragmentClass, args, null);
    }

    public <T extends Fragment> T pushFragment(@NotNull Class<T> fragmentClass, Bundle args, @Nullable String backStackName)
    {
        return pushFragment(fragmentClass, args, backStackName, DEFAULT_ADD_TO_BACK_STACK);
    }

    public <T extends Fragment> T pushFragment(@NotNull Class<T> fragmentClass, Bundle args, @Nullable String backStackName,
            Boolean shouldAddToBackStack)
    {
        return pushFragment(fragmentClass, args, DEFAULT_FRAGMENT_ANIMATION, backStackName, shouldAddToBackStack, DEFAULT_SHOW_HOME_KEY_AS_UP);
    }

    public <T extends Fragment> T pushFragment(@NotNull Class<T> fragmentClass, Bundle args, @Nullable int[] anim, @Nullable String backStackName,
            Boolean shouldAddToBackStack, Boolean showHomeAsUp)
    {
        resetBackPressCount();

        Timber.d("Push Keyboard visible %s", DeviceUtil.isKeyboardVisible(activity));
        Timber.d("Pushing fragment %s", fragmentClass.getSimpleName());

        setupHomeAsUp(fragmentClass, args, showHomeAsUp);

        Fragment fragment = Fragment.instantiate(activity, fragmentClass.getName(), args);
        fragment.setArguments(args);
        FragmentTransaction transaction = manager.beginTransaction();

        if (anim != null)
        {
            transaction.setCustomAnimations(anim[0], anim[1], anim[2], anim[3]);
        }

        if (backStackName == null)
        {
            backStackName = fragmentClass.getName();
        }

        FragmentTransaction ft = transaction.replace(fragmentContentId, fragment);
        if (shouldAddToBackStack)
        {
            ft.addToBackStack(backStackName);
        }
        ft.commitAllowingStateLoss();

        @SuppressWarnings("unchecked")
        T returnFragment = (T) fragment;
        return returnFragment;
    }

    private void setupHomeAsUp(Class<? extends Fragment> fragmentClass, Bundle args, boolean showHomeAsUp)
    {
        if (args != null)
        {
            if (DashboardFragment.class.isAssignableFrom(fragmentClass))
            {
                ActionBarOwnerMixin.putKeyShowHomeAsUp(args, showHomeAsUp);
            }
            else if (DashboardPreferenceFragment.class.isAssignableFrom(fragmentClass))
            {
                ActionBarOwnerMixin.putKeyShowHomeAsUp(args, showHomeAsUp);
            }
        }
    }

    public void popFragment(String backStackName)
    {
        Timber.d("Pop Keyboard visible %b", DeviceUtil.isKeyboardVisible(activity));
        Timber.d("Popping fragment, count: %d", manager.getBackStackEntryCount());

        if (activity != null)
        {
            if (DeviceUtil.isKeyboardVisible(activity))
            {
                DeviceUtil.dismissKeyboard(activity);
            }

            if (isBackStackEmpty())
            {
                if (backPressedCount > 0)
                {
                    resetBackPressCount();
                    activity.finish();
                }
                else
                {
                    ++backPressedCount;
                    THToast.show(R.string.press_back_again_to_exit);
                }
                return;
            }

            if (backStackName == null)
            {
                manager.popBackStack();
            }
            else
            {
                manager.popBackStack(backStackName, 0);
            }
        }
    }

    private void sendAppToBackground()
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public boolean isBackStackEmpty()
    {
        return manager.getBackStackEntryCount() == 0;
    }

    protected void resetBackPressCount()
    {
        backPressedCount = 0;
    }

    public Fragment getCurrentFragment()
    {
        return manager.findFragmentById(R.id.realtabcontent);
    }
}
