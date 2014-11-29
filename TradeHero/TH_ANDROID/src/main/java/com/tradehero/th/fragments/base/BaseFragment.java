package com.tradehero.th.fragments.base;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import com.tradehero.th.inject.HierarchyInjector;
import rx.Subscription;
import timber.log.Timber;

public class BaseFragment extends Fragment
{
    private static final String BUNDLE_KEY_HAS_OPTION_MENU = BaseFragment.class.getName() + ".hasOptionMenu";
    private static final String BUNDLE_KEY_IS_OPTION_MENU_VISIBLE = BaseFragment.class.getName() + ".isOptionMenuVisible";

    public static final boolean DEFAULT_HAS_OPTION_MENU = true;
    public static final boolean DEFAULT_IS_OPTION_MENU_VISIBLE = true;

    protected boolean hasOptionMenu;
    protected boolean isOptionMenuVisible;

    protected ActionBarOwnerMixin actionBarOwnerMixin;

    public static boolean getHasOptionMenu(@Nullable Bundle args)
    {
        if (args == null)
        {
            return DEFAULT_HAS_OPTION_MENU;
        }
        return args.getBoolean(BUNDLE_KEY_HAS_OPTION_MENU, DEFAULT_HAS_OPTION_MENU);
    }

    public static void putIsOptionMenuVisible(@NonNull Bundle args, boolean optionMenuVisible)
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

        HierarchyInjector.inject(this);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        actionBarOwnerMixin = ActionBarOwnerMixin.of(this);

        isOptionMenuVisible = getIsOptionMenuVisible(getArguments());
        hasOptionMenu = getHasOptionMenu(getArguments());
        setHasOptionsMenu(hasOptionMenu);
    }

    @Override public void onDestroy()
    {
        actionBarOwnerMixin.onDestroy();
        super.onDestroy();
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
        if (getActivity() != null)
        {
            return getActivity().getActionBar();
        }
        else
        {
            Timber.e(new Exception(), "getActivity is Null");
            return null;
        }
    }

    protected final void setActionBarTitle(String string)
    {
        actionBarOwnerMixin.setActionBarTitle(string);
    }

    protected final void setActionBarTitle(@StringRes int stringResId)
    {
        actionBarOwnerMixin.setActionBarTitle(stringResId);
    }

    protected void setActionBarSubtitle(@StringRes int subTitleResId)
    {
        actionBarOwnerMixin.setActionBarSubtitle(subTitleResId);
    }

    protected void setActionBarSubtitle(String subtitle)
    {
        actionBarOwnerMixin.setActionBarSubtitle(subtitle);
    }

    protected void unsubscribe(@Nullable Subscription subscription)
    {
        if (subscription != null && !subscription.isUnsubscribed())
        {
            subscription.unsubscribe();
        }
    }
}
