package com.tradehero.th.fragments.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.preference.PreferenceFragment;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import com.special.residemenu.ResideMenu;
import com.tradehero.th.BottomTabsQuickReturnListViewListener;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Subscription;

public class DashboardPreferenceFragment extends PreferenceFragment
{
    @Inject Lazy<ResideMenu> resideMenuLazy;
    @Inject Lazy<DashboardNavigator> navigator;
    @Inject @BottomTabsQuickReturnListViewListener protected Lazy<AbsListView.OnScrollListener> dashboardBottomTabsScrollListener;
    private ActionBarOwnerMixin actionBarOwnerMixin;

    @Override public void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);

        actionBarOwnerMixin = ActionBarOwnerMixin.of(this);
    }

    @Override public void onDestroy()
    {
        actionBarOwnerMixin.onDestroy();
        actionBarOwnerMixin = null;
        super.onDestroy();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        actionBarOwnerMixin.onCreateOptionsMenu(menu, inflater);
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
        }
        return super.onOptionsItemSelected(item);
    }

    // Should inject navigator instead of using this method
    @Deprecated
    public DashboardNavigator getNavigator()
    {
        return navigator.get();
    }

    protected void unsubscribe(@Nullable Subscription subscription)
    {
        if (subscription != null && !subscription.isUnsubscribed())
        {
            subscription.unsubscribe();
        }
    }
}
