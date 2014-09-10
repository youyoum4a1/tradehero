package com.tradehero.th.fragments.base;

import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ActionBarOwnerMixin
{
    private static final String BUNDLE_KEY_TITLE = DashboardFragment.class.getName() + ".title";
    private static final String BUNDLE_KEY_SHOW_HOME_AS_UP = DashboardFragment.class.getName() + ".show_home_as_up";
    private static final boolean DEFAULT_SHOW_HOME_AS_UP = true;

    private final SherlockFragment fragment;
    private final ActionBar actionBar;

    public static ActionBarOwnerMixin of(SherlockFragment fragment)
    {
        return new ActionBarOwnerMixin(fragment);
    }

    public static void putKeyShowHomeAsUp(@NotNull Bundle args, boolean showAsUp)
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

    public static void putActionBarTitle(Bundle args, String title)
    {
        if (args != null)
        {
            args.putString(BUNDLE_KEY_TITLE, title);
        }
    }

    private ActionBarOwnerMixin(SherlockFragment fragment)
    {
        this.fragment = fragment;
        this.actionBar = fragment.getSherlockActivity().getSupportActionBar();
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

        if(actionBar != null)
        {
            if (shouldShowHomeAsUp())
            {
                actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                        | ActionBar.DISPLAY_SHOW_TITLE
                        | ActionBar.DISPLAY_SHOW_HOME);
            }
            else
            {
                actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE
                        | ActionBar.DISPLAY_SHOW_HOME
                        | ActionBar.DISPLAY_USE_LOGO);
                actionBar.setLogo(R.drawable.icn_actionbar_hamburger);
            }
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    protected void setActionBarTitle(int titleResId)
    {
        if (actionBar != null)
        {
            actionBar.setTitle(titleResId);
        }
    }

    protected void setActionBarTitle(String title)
    {
        if (actionBar != null)
        {
            actionBar.setTitle(title);
        }
    }

    public boolean shouldShowHomeAsUp()
    {
        return getKeyShowHomeAsUp(fragment.getArguments());
    }

    public void setActionBarSubtitle(int subTitleResId)
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
}
