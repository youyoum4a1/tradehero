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
import android.view.MenuItem;
import android.widget.AbsListView;
import com.etiennelawlor.quickreturn.library.views.NotifyingScrollView;
import com.special.residemenu.ResideMenu;
import com.tradehero.th.BottomTabsQuickReturnListViewListener;
import com.tradehero.th.BottomTabsQuickReturnScrollViewListener;
import com.tradehero.th.R;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.utils.AlertDialogUtil;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Subscription;
import rx.internal.util.SubscriptionList;
import timber.log.Timber;

abstract public class DashboardFragment extends Fragment
{
    private static final String BUNDLE_KEY_HAS_OPTION_MENU = DashboardFragment.class.getName() + ".hasOptionMenu";
    private static final String BUNDLE_KEY_IS_OPTION_MENU_VISIBLE = DashboardFragment.class.getName() + ".isOptionMenuVisible";

    public static final boolean DEFAULT_HAS_OPTION_MENU = true;
    public static final boolean DEFAULT_IS_OPTION_MENU_VISIBLE = true;

    protected boolean hasOptionMenu;
    protected boolean isOptionMenuVisible;

    protected ActionBarOwnerMixin actionBarOwnerMixin;
    @NonNull protected SubscriptionList onStopSubscriptions;


    @Inject protected Lazy<DashboardNavigator> navigator;
    @Inject Lazy<ResideMenu> resideMenuLazy;

    @Inject @BottomTabsQuickReturnListViewListener protected Lazy<AbsListView.OnScrollListener> dashboardBottomTabsListViewScrollListener;
    @Inject @BottomTabsQuickReturnScrollViewListener protected Lazy<NotifyingScrollView.OnScrollChangedListener>
            dashboardBottomTabScrollViewScrollListener;

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

    @Override public void onStart()
    {
        super.onStart();
        this.onStopSubscriptions = new SubscriptionList();
    }

    @Override public void onStop()
    {
        this.onStopSubscriptions.unsubscribe();
        this.onStopSubscriptions = new SubscriptionList();
        super.onStop();
    }

    @Override public void onDestroy()
    {
        actionBarOwnerMixin.onDestroy();
        super.onDestroy();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
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

        if (this instanceof WithTutorial)
        {
            inflater.inflate(R.menu.menu_with_tutorial, menu);
        }

        actionBarOwnerMixin.onCreateOptionsMenu(menu, inflater);

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

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                if (actionBarOwnerMixin.shouldShowHomeAsUp())
                {
                    navigator.get().popFragment();
                }
                else
                {
                    resideMenuLazy.get().openMenu();
                }
                return true;

            case R.id.menu_info:
                handleInfoMenuItemClicked();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void handleInfoMenuItemClicked()
    {
        if (this instanceof WithTutorial)
        {
            AlertDialogUtil.popTutorialContent(getActivity(), ((WithTutorial) this).getTutorialLayout());
        }
        else
        {
            Timber.d("%s is not implementing WithTutorial interface, but has info menu", getClass().getName());
        }
    }

    public <T extends Fragment> boolean allowNavigateTo(@NonNull Class<T> fragmentClass, Bundle args)
    {
        return true;
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
