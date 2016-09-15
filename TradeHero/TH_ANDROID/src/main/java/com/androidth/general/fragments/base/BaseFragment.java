package com.androidth.general.fragments.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.androidth.general.fragments.DashboardNavigator;
import com.androidth.general.fragments.tutorial.WithTutorial;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.utils.AlertDialogUtil;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Subscription;
import rx.internal.util.SubscriptionList;
import timber.log.Timber;

public class BaseFragment extends Fragment
{
    private static final String BUNDLE_KEY_HAS_OPTION_MENU = BaseFragment.class.getName() + ".hasOptionMenu";
    private static final String BUNDLE_KEY_IS_OPTION_MENU_VISIBLE = BaseFragment.class.getName() + ".isOptionMenuVisible";

    public static final boolean DEFAULT_HAS_OPTION_MENU = true;
    public static final boolean DEFAULT_IS_OPTION_MENU_VISIBLE = true;
    private static final int MENU_GROUP_HELP = "MENU_GROUP_HELP".hashCode();

    protected boolean hasOptionMenu;
    protected boolean isOptionMenuVisible;

    protected ActionBarOwnerMixin actionBarOwnerMixin;
    protected SubscriptionList onStopSubscriptions;
    protected SubscriptionList onDestroyViewSubscriptions;
    protected SubscriptionList onDestroyOptionsMenuSubscriptions;
    protected SubscriptionList onDestroySubscriptions;

    @Inject protected Lazy<DashboardNavigator> navigator;

    public static void setHasOptionMenu(@NonNull Bundle args, boolean hasOptionMenu)
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

    @CallSuper
    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        HierarchyInjector.inject(this);
    }

    @CallSuper
    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (navigator == null)
        {
            HierarchyInjector.inject(this);
        }
        actionBarOwnerMixin = ActionBarOwnerMixin.of(this);

        isOptionMenuVisible = getIsOptionMenuVisible(getArguments());
        hasOptionMenu = getHasOptionMenu(getArguments());
        setHasOptionsMenu(hasOptionMenu);
        onDestroySubscriptions = new SubscriptionList();
    }

    @CallSuper
    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        onDestroyViewSubscriptions = new SubscriptionList();
    }

    @CallSuper
    @Override public void onStart()
    {
        super.onStart();
        this.onStopSubscriptions = new SubscriptionList();
    }

    @CallSuper
    @Override public void onStop()
    {
        this.onStopSubscriptions.unsubscribe();
        super.onStop();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        // Do not unsubscribe here
    }

    @CallSuper
    @Override public void onDestroyView()
    {
        onDestroyViewSubscriptions.unsubscribe();
        super.onDestroyView();
    }

    @CallSuper
    @Override public void onDestroy()
    {
        actionBarOwnerMixin.onDestroy();
        onDestroySubscriptions.unsubscribe();
        super.onDestroy();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        onDestroyOptionsMenuSubscriptions = new SubscriptionList();

        if (!hasOptionMenu)
        {
            return;
        }

        if (getParentFragment() == null)
        {
            /**
             * If the fragment is being hosted by another fragment,
             * we don't need to setup the home/drawer menu button.
             * We let the parent fragment to handle the menu, such that there is no conflict
             */
            if (isOptionMenuVisible)
            {
                showSupportActionBar();
            }
            else
            {
                hideSupportActionBar();
            }

            actionBarOwnerMixin.onCreateOptionsMenu(menu, inflater);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    private int getMenuHelpID()
    {
        return (getClass().getName() + ".help").hashCode();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                if (actionBarOwnerMixin.shouldShowHomeAsUp())
                {
                    navigator.get().popFragment();
                    return true;
                }
                break;
        }
        if (item.getItemId() == getMenuHelpID())
        {
            return handleInfoMenuItemClicked();
        }
        return super.onOptionsItemSelected(item);
    }

    @CallSuper
    @Override public void onDestroyOptionsMenu()
    {
        if (onDestroyOptionsMenuSubscriptions != null)
        // We need this test as it appears some SDKs call destroy before calling create
        {
            onDestroyOptionsMenuSubscriptions.unsubscribe();
        }
        super.onDestroyOptionsMenu();
    }

    protected boolean handleInfoMenuItemClicked()
    {
        if (this instanceof WithTutorial)
        {
            AlertDialogUtil.popTutorialContent(
                    getActivity(),
                    ((WithTutorial) this).getTutorialLayout());
            return true;
        }
        else
        {
            Timber.d("%s is not implementing WithTutorial interface, but has info menu", getClass().getName());
            return false;
        }
    }

    @Nullable protected ActionBar getSupportActionBar()
    {
        if (getActivity() != null)
        {
            return ((AppCompatActivity) getActivity()).getSupportActionBar();
        }
        else
        {
            Timber.e(new Exception(), "getActivity is Null");
            return null;
        }
    }

    protected void hideSupportActionBar()
    {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null)
        {
            supportActionBar.hide();
        }
    }

    protected void showSupportActionBar()
    {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null)
        {
            supportActionBar.show();
        }
    }

    public final void setActionBarTitle(String string)
    {
        actionBarOwnerMixin.setActionBarTitle(string);
//        actionBarOwnerMixin.setCustomView(null);
    }

    public final void setActionBarColor(String hexColor)
    {
        String color = hexColor.startsWith("#") ? hexColor : "#".concat(hexColor);
        actionBarOwnerMixin.setActionBarColor(color);
    }

    public final void setActionBarColor(int resourceId)
    {
        actionBarOwnerMixin.setActionBarColor(resourceId);
    }

    public final void setActionBarTitle(@StringRes int stringResId)
    {
        actionBarOwnerMixin.setActionBarTitle(stringResId);
    }

    public void setActionBarSubtitle(@StringRes int subTitleResId)
    {
        actionBarOwnerMixin.setActionBarSubtitle(subTitleResId);
    }

    public void setActionBarSubtitle(String subtitle)
    {
        actionBarOwnerMixin.setActionBarSubtitle(subtitle);
    }

    protected void setActionBarCustomImage(Activity activity, String url, boolean hasOtherItems){
        actionBarOwnerMixin.setActionBarCustomImage(getSupportActionBar(), activity, url, hasOtherItems);
    }

    protected void unsubscribe(@Nullable Subscription subscription)
    {
        if (subscription != null && !subscription.isUnsubscribed())
        {
            subscription.unsubscribe();
        }
    }

    public <T extends Fragment> boolean allowNavigateTo(@NonNull Class<T> fragmentClass, Bundle args)
    {
        return true;
    }
}
