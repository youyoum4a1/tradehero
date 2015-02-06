package com.tradehero.th.fragments.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import com.special.residemenu.ResideMenu;
import com.tradehero.th.BottomTabsQuickReturnListViewListener;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;
import dagger.Lazy;
import javax.inject.Inject;
import rx.internal.util.SubscriptionList;

public class DashboardPreferenceFragment extends PreferenceFragment
{
    @Inject Lazy<ResideMenu> resideMenuLazy;
    @Inject Lazy<DashboardNavigator> navigator;
    @Inject @BottomTabsQuickReturnListViewListener protected Lazy<AbsListView.OnScrollListener> dashboardBottomTabsScrollListener;
    private ActionBarOwnerMixin actionBarOwnerMixin;
    @NonNull protected SubscriptionList onStopSubscriptions;

    @Override public void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);

        actionBarOwnerMixin = ActionBarOwnerMixin.of(this);
        this.onStopSubscriptions = new SubscriptionList();
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

    @Override public void onStop()
    {
        this.onStopSubscriptions.unsubscribe();
        this.onStopSubscriptions = new SubscriptionList();
        super.onStop();
    }

    // Should inject navigator instead of using this method
    @Deprecated
    public DashboardNavigator getNavigator()
    {
        return navigator.get();
    }
}
