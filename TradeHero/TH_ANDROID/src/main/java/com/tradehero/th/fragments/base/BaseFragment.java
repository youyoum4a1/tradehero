package com.tradehero.th.fragments.base;

import android.app.Activity;
import android.os.Bundle;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th.utils.DaggerUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class BaseFragment extends SherlockFragment
{
    private static final String BUNDLE_KEY_HAS_OPTION_MENU = BaseFragment.class.getName() + ".hasOptionMenu";
    private static final String BUNDLE_KEY_IS_OPTION_MENU_VISIBLE = BaseFragment.class.getName() + ".isOptionMenuVisible";

    public static final boolean DEFAULT_HAS_OPTION_MENU = true;
    public static final boolean DEFAULT_IS_OPTION_MENU_VISIBLE = true;

    protected boolean hasOptionMenu;
    protected boolean isOptionMenuVisible;

    public static void putHasOptionMenu(@NotNull Bundle args, boolean hasOptionMenu)
    {
        args.putBoolean(BUNDLE_KEY_HAS_OPTION_MENU, hasOptionMenu);
    }

    public static boolean getHasOptionMenu(@Nullable Bundle args)
    {
        if (args == null)
        {
            return DEFAULT_HAS_OPTION_MENU;
        }
        return args.getBoolean(BUNDLE_KEY_HAS_OPTION_MENU, DEFAULT_HAS_OPTION_MENU);
    }

    public static void putIsOptionMenuVisible(@NotNull Bundle args, boolean optionMenuVisible)
    {
        args.putBoolean(BUNDLE_KEY_IS_OPTION_MENU_VISIBLE, optionMenuVisible);
    }

    public static boolean getIsOptionMenuVisible(@Nullable Bundle args)
    {
        if (args == null)
        {
            return DEFAULT_IS_OPTION_MENU_VISIBLE;
        }
        return args.getBoolean(BUNDLE_KEY_IS_OPTION_MENU_VISIBLE, DEFAULT_IS_OPTION_MENU_VISIBLE);
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        DaggerUtils.inject(this);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        isOptionMenuVisible = getIsOptionMenuVisible(getArguments());
        hasOptionMenu = getHasOptionMenu(getArguments());
        setHasOptionsMenu(hasOptionMenu);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            if (isOptionMenuVisible)
            {
                actionBar.show();
            }
            else
            {
                actionBar.hide();
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Nullable protected ActionBar getSupportActionBar()
    {
        if (getSherlockActivity() != null)
        {
            ActionBar actionbar = getSherlockActivity().getSupportActionBar();
            return actionbar;
        }
        else
        {
            Timber.e(new Exception(),"getActivity is Null");
            return null;
        }
    }

    protected void setActionBarTitle(int titleresId)
    {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setTitle(titleresId);
        }
    }

    protected void setActionBarTitle(String title)
    {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setTitle(title);
        }
    }

    protected void setActionBarSubtitle(int subtitleResId)
    {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setSubtitle(subtitleResId);
        }
    }

    protected void setActionBarSubtitle(String subtitle)
    {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setSubtitle(subtitle);
        }
    }
}
