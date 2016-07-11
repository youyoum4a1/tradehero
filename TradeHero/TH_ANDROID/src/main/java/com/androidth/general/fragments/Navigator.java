package com.androidth.general.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.R;
import com.androidth.general.fragments.base.ActionBarOwnerMixin;
import com.androidth.general.fragments.base.DashboardFragment;
import com.androidth.general.utils.DeviceUtil;
import timber.log.Timber;

class Navigator<ActivityType extends Activity>
{
    private static final String BUNDLE_KEY_RETURN_FRAGMENT = Navigator.class.getName() + ".returnFragment";
    private static final boolean DEFAULT_SHOW_HOME_KEY_AS_UP = true;

    public static final int[] DEFAULT_FRAGMENT_ANIMATION = new int[] {
            R.anim.slide_right_in, R.anim.slide_left_out,
            R.anim.slide_left_in, R.anim.slide_right_out
    };

    protected final ActivityType activity;
    protected final FragmentManager manager;
    private int fragmentContentId;
    private int backPressedCount;
    private final int minimumBackstackSize;

    //<editor-fold desc="Argument passing">
    public static void putReturnFragment(@NonNull Bundle args, @NonNull String fragmentClassName)
    {
        args.putString(BUNDLE_KEY_RETURN_FRAGMENT, fragmentClassName);
    }

    @Nullable public static String getReturnFragment(@NonNull Bundle args)
    {
        return args.getString(BUNDLE_KEY_RETURN_FRAGMENT);
    }
    //</editor-fold>

    //<editor-fold desc="Constructors">
    public Navigator(ActivityType activity, FragmentManager manager, int fragmentContentId, int minimumBackstackSize)
    {
        this.activity = activity;
        this.manager = manager;
        this.fragmentContentId = fragmentContentId;
        this.minimumBackstackSize = minimumBackstackSize;
    }
    //</editor-fold>

    public <T extends Fragment> T pushFragment(@NonNull Class<T> fragmentClass)
    {
        return pushFragment(fragmentClass, new Bundle());
    }

    public <T extends Fragment> T pushFragment(@NonNull Class<T> fragmentClass, Bundle args)
    {
        return pushFragment(fragmentClass, args, null);
    }

    public <T extends Fragment> T pushFragment(@NonNull Class<T> fragmentClass, Bundle args, @Nullable String backStackName)
    {
        return pushFragment(fragmentClass, args, DEFAULT_FRAGMENT_ANIMATION, backStackName, DEFAULT_SHOW_HOME_KEY_AS_UP);
    }

    public <T extends Fragment> T pushFragment(@NonNull Class<T> fragmentClass, Bundle args, @Nullable int[] anim, @Nullable String backStackName,
            boolean showHomeAsUp)
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

        transaction
                .replace(fragmentContentId, fragment)
                .addToBackStack(backStackName)
                .commitAllowingStateLoss();

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
        }
    }


    public void popFragment()
    {
        Fragment currentDashboardFragment = manager.findFragmentById(fragmentContentId);

        String backStackName = null;
        if (currentDashboardFragment != null && currentDashboardFragment.getArguments() != null)
        {
            Bundle args = currentDashboardFragment.getArguments();
            backStackName = args.getString(BUNDLE_KEY_RETURN_FRAGMENT);
        }
        popFragment(backStackName);
    }
    public String getBackStackName(){
        Fragment currentDashboardFragment = manager.findFragmentById(fragmentContentId);

        String backStackName = null;
        if (currentDashboardFragment != null && currentDashboardFragment.getArguments() != null)
        {
            Bundle args = currentDashboardFragment.getArguments();
            backStackName = args.getString(BUNDLE_KEY_RETURN_FRAGMENT);
        }
        return backStackName;
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
                if (backPressedCount >= minimumBackstackSize)
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
        return manager.getBackStackEntryCount() <= minimumBackstackSize;
    }

    protected void resetBackPressCount()
    {
        backPressedCount = 0;
    }

    @Nullable public Fragment getCurrentFragment()
    {
        return manager.findFragmentById(fragmentContentId);
    }
}
