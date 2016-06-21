package com.androidth.general.fragments.base;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import com.androidth.general.R;

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

    public void setActionBarColor(String hexColor)
    {
        if(actionBar != null) {
            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor(hexColor));
            actionBar.setBackgroundDrawable(colorDrawable);
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

    public void setCustomView(View view)
    {
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(view);
    }
}
