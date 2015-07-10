package com.tradehero.th.fragments.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.ViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.activities.BaseActivity;
import timber.log.Timber;

public class ActionBarOwnerMixin
{
    private static final String BUNDLE_KEY_TITLE = ActionBarOwnerMixin.class.getName() + ".title";
    private static final String BUNDLE_KEY_TOUCH_HOME = ActionBarOwnerMixin.class.getName() + ".touchHome";
    private static final String BUNDLE_KEY_SHOW_HOME = ActionBarOwnerMixin.class.getName() + ".showHome";
    private static final String BUNDLE_KEY_SHOW_HOME_AS_UP = ActionBarOwnerMixin.class.getName() + ".showHomeAsUp";
    private static final boolean DEFAULT_TOUCH_HOME = true;
    private static final boolean DEFAULT_SHOW_HOME = true;
    private static final boolean DEFAULT_SHOW_HOME_AS_UP = true;

    private final Fragment fragment;
    private final ActionBar actionBar;

    @NonNull public static ActionBarOwnerMixin of(@NonNull Fragment fragment)
    {
        return new ActionBarOwnerMixin(fragment);
    }

    //<editor-fold desc="Arguments Passing">
    public static void putKeyTouchHome(@NonNull Bundle args, boolean touchHome)
    {
        args.putBoolean(BUNDLE_KEY_TOUCH_HOME, touchHome);
    }

    protected static boolean getKeyTouchHome(@Nullable Bundle args)
    {
        if (args == null)
        {
            return DEFAULT_TOUCH_HOME;
        }
        return args.getBoolean(BUNDLE_KEY_TOUCH_HOME, DEFAULT_TOUCH_HOME);
    }

    public static void putKeyShowHome(@NonNull Bundle args, boolean showHome)
    {
        args.putBoolean(BUNDLE_KEY_SHOW_HOME, showHome);
    }

    protected static boolean getKeyShowHome(@Nullable Bundle args)
    {
        if (args == null)
        {
            return DEFAULT_SHOW_HOME;
        }
        return args.getBoolean(BUNDLE_KEY_SHOW_HOME, DEFAULT_SHOW_HOME);
    }

    public static void putKeyShowHomeAsUp(@NonNull Bundle args, boolean showAsUp)
    {
        args.putBoolean(BUNDLE_KEY_SHOW_HOME_AS_UP, showAsUp);
    }

    protected static boolean getKeyShowHomeAsUp(@Nullable Bundle args)
    {
        if (args == null)
        {
            return DEFAULT_SHOW_HOME_AS_UP;
        }
        return args.getBoolean(BUNDLE_KEY_SHOW_HOME_AS_UP, DEFAULT_SHOW_HOME_AS_UP);
    }

    public ActionBar getActionBar()
    {
        return actionBar;
    }

    public static void putActionBarTitle(Bundle args, String title)
    {
        if (args != null)
        {
            args.putString(BUNDLE_KEY_TITLE, title);
        }
    }
    //</editor-fold>

    private ActionBarOwnerMixin(@NonNull Fragment fragment)
    {
        this.fragment = fragment;
        this.actionBar = ((AppCompatActivity) fragment.getActivity()).getSupportActionBar();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        Bundle argument = fragment.getArguments();
        if (argument != null && argument.containsKey(BUNDLE_KEY_TITLE))
        {
            String title = argument.getString(BUNDLE_KEY_TITLE);

            if (title != null && !title.isEmpty())
            {
                setActionBarTitle(title);
            }
        }

        if (actionBar != null && shouldTouchHome())
        {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                    | ActionBar.DISPLAY_SHOW_TITLE
                    | ActionBar.DISPLAY_SHOW_HOME);
            if (!shouldShowHome())
            {
                actionBar.setHomeAsUpIndicator(null);
                actionBar.setDisplayHomeAsUpEnabled(false);
            }
            else if (shouldShowHomeAsUp())
            {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(R.drawable.ic_actionbar_back);
            }
            else
            {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(R.drawable.icn_actionbar_hamburger);
            }
        }
    }

    public void setActionBarTitle(@StringRes int titleResId)
    {
        if (actionBar != null)
        {
            actionBar.setTitle(titleResId);
        }
    }

    public boolean shouldTouchHome()
    {
        return getKeyTouchHome(fragment.getArguments());
    }

    public boolean shouldShowHome()
    {
        return getKeyShowHome(fragment.getArguments());
    }

    public boolean shouldShowHomeAsUp()
    {
        return getKeyShowHomeAsUp(fragment.getArguments());
    }

    public void setActionBarSubtitle(@StringRes int subTitleResId)
    {
        if (actionBar != null)
        {
            actionBar.setSubtitle(subTitleResId);
        }
    }

    public void onDestroy()
    {
        // nothing for now
    }

    public void setActionBarSubtitle(String subtitle)
    {
        if (actionBar != null)
        {
            actionBar.setSubtitle(subtitle);
        }
    }

    public void setActionBarTitle(String title)
    {
        if (actionBar != null)
        {
            actionBar.setTitle(title);
        }
    }

    public void addAnimatorView(
            @NonNull BaseActivity activity,
            @IdRes int animatorId,
            @NonNull View view,
            int index)
    {
        Toolbar toolbar = activity.getToolbar();
        if (toolbar == null)
        {
            return;
        }

        ViewAnimator animator = (ViewAnimator) toolbar.findViewById(animatorId);
        if (animator == null)
        {
            Timber.e(new NullPointerException(), "There was no animator");
            return;
        }
        animator.addView(view, index);
        animator.setVisibility(View.VISIBLE);
    }

    public void removeView(
            @NonNull BaseActivity activity,
            @IdRes int animatorId,
            int index)
    {
        Toolbar toolbar = activity.getToolbar();
        if (toolbar == null)
        {
            return;
        }

        ViewAnimator animator = (ViewAnimator) toolbar.findViewById(animatorId);
        if (animator == null)
        {
            Timber.e(new NullPointerException(), "There was no animator");
            return;
        }
        animator.removeViewAt(index);
    }

    ///**
    // * Set the spinner adapter and OnItemSelectedListener of the Spinner.
    // *
    // * @param toolbarSpinnerResId resource id of the spinner.
    // */
    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    //public void configureSpinner(
    //        @NonNull BaseActivity activity,
    //        @IdRes int toolbarSpinnerResId,
    //        ArrayAdapter adapter,
    //        AdapterView.OnItemSelectedListener listener,
    //        int selectedPosition)
    //{
    //    Toolbar toolbar = activity.getToolbar();
    //    if (toolbar == null)
    //    {
    //        return;
    //    }
    //
    //    Spinner toolbarSpinner = (Spinner) toolbar.findViewById(toolbarSpinnerResId);
    //    if (toolbarSpinner == null)
    //    {
    //        return;
    //    }
    //
    //    toolbarSpinner.setAdapter(adapter);
    //    if ((selectedPosition >= 0) && (selectedPosition < adapter.getCount()))
    //    {
    //        toolbarSpinner.setSelection(selectedPosition);
    //    }
    //    toolbarSpinner.setOnItemSelectedListener(listener);
    //    toolbarSpinner.setVisibility(View.VISIBLE);
    //}
    //
    //public void setSpinnerSelection(@NonNull BaseActivity activity, @IdRes int toolbarSpinnerResId, int index)
    //{
    //    Toolbar toolbar = activity.getToolbar();
    //    if (toolbar == null)
    //    {
    //        return;
    //    }
    //
    //    Spinner toolbarSpinner = (Spinner) toolbar.findViewById(toolbarSpinnerResId);
    //
    //    if (toolbarSpinner == null)
    //    {
    //        return;
    //    }
    //    toolbarSpinner.setSelection(index);
    //}
    //
    //public void hideToolbarSpinner(@NonNull BaseActivity activity, @IdRes int toolbarSpinnerResId)
    //{
    //    Toolbar toolbar = activity.getToolbar();
    //    if (toolbar == null)
    //    {
    //        return;
    //    }
    //
    //    Spinner toolbarSpinner = (Spinner) toolbar.findViewById(toolbarSpinnerResId);
    //
    //    if (toolbarSpinner == null)
    //    {
    //        return;
    //    }
    //    toolbarSpinner.setVisibility(View.GONE);
    //}
    //
    //public void showToolbarSpinner(@NonNull BaseActivity activity, @IdRes int toolbarSpinnerResId)
    //{
    //    Toolbar toolbar = activity.getToolbar();
    //    if (toolbar == null)
    //    {
    //        return;
    //    }
    //
    //    Spinner toolbarSpinner = (Spinner) toolbar.findViewById(toolbarSpinnerResId);
    //
    //    if (toolbarSpinner == null)
    //    {
    //        return;
    //    }
    //    toolbarSpinner.setVisibility(View.VISIBLE);
    //}

    public void setCustomView(View view)
    {
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(view);
    }
}
