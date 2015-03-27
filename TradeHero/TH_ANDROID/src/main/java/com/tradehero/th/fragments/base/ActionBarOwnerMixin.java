package com.tradehero.th.fragments.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.activities.BaseActivity;
import java.lang.annotation.Target;

public class ActionBarOwnerMixin
{
    private static final String BUNDLE_KEY_TITLE = ActionBarOwnerMixin.class.getName() + ".title";
    private static final String BUNDLE_KEY_SHOW_HOME_AS_UP = ActionBarOwnerMixin.class.getName() + ".show_home_as_up";
    private static final boolean DEFAULT_SHOW_HOME_AS_UP = true;

    private final Fragment fragment;
    private final ActionBar actionBar;

    private Spinner toolbarSpinner;

    public static ActionBarOwnerMixin of(Fragment fragment)
    {
        return new ActionBarOwnerMixin(fragment);
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

    public static void putActionBarTitle(Bundle args, String title)
    {
        if (args != null)
        {
            args.putString(BUNDLE_KEY_TITLE, title);
        }
    }

    private ActionBarOwnerMixin(Fragment fragment)
    {
        this.fragment = fragment;
        this.actionBar = ((ActionBarActivity)fragment.getActivity()).getSupportActionBar();
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
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                    | ActionBar.DISPLAY_SHOW_TITLE
                    | ActionBar.DISPLAY_SHOW_HOME);
            if (shouldShowHomeAsUp())
            {
                actionBar.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            }
            else
            {
                actionBar.setHomeAsUpIndicator(R.drawable.icn_actionbar_hamburger);
            }
        }
    }

    protected void setActionBarTitle(@StringRes int titleResId)
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

    /**
     * Set the spinner adapter and OnItemSelectedListener of the Spinner.
     * @param toolbarSpinnerResId resource id of the spinner.
     * @param adapter
     * @param listener
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void configureSpinner(int toolbarSpinnerResId, ArrayAdapter adapter, AdapterView.OnItemSelectedListener listener, int selectedPosition) {
        Toolbar toolbar = ((BaseActivity) fragment.getActivity()).getToolbar();
        if (toolbar == null) {
            return;
        }

        toolbarSpinner = (Spinner) toolbar.findViewById(toolbarSpinnerResId);
        if (toolbarSpinner == null) {
            return;
        }

        toolbarSpinner.setAdapter(adapter);
        if ((selectedPosition >= 0) && (selectedPosition < adapter.getCount()))
        {
            toolbarSpinner.setSelection(selectedPosition);
        }
        toolbarSpinner.setOnItemSelectedListener(listener);
        toolbarSpinner.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TypedValue value = new TypedValue();
            fragment.getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, value, true);
            int offset = (int) fragment.getActivity().getResources().getDimension(value.resourceId);
            toolbarSpinner.setDropDownVerticalOffset(offset);
        }
    }

    public void hideToolbarSpinner() {
        if (toolbarSpinner == null) {
            return;
        }
        toolbarSpinner.setVisibility(View.GONE);
    }

    public void showToolbarSpinner()
    {
        if (toolbarSpinner == null) {
            return;
        }
        toolbarSpinner.setVisibility(View.VISIBLE);
    }
}
