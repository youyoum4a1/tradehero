package com.tradehero.th.fragments.base;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th.utils.DaggerUtils;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class BaseFragment extends SherlockFragment
{
    public static final String BUNDLE_KEY_HAS_OPTION_MENU = BaseFragment.class.getName() + ".hasOptionMenu";
    public static final String BUNDLE_KEY_IS_OPTION_MENU_VISIBLE = BaseFragment.class.getName() + ".isOptionMenuVisible";

    protected boolean hasOptionMenu = true;
    protected boolean isOptionMenuVisible = true;

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        DaggerUtils.inject(this);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null)
        {
            hasOptionMenu = args.getBoolean(BUNDLE_KEY_HAS_OPTION_MENU, hasOptionMenu);
            isOptionMenuVisible = args.getBoolean(BUNDLE_KEY_IS_OPTION_MENU_VISIBLE, isOptionMenuVisible);
        }
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
            Timber.e("getActivity is Null on %s", Log.getStackTraceString(new Exception()));
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
