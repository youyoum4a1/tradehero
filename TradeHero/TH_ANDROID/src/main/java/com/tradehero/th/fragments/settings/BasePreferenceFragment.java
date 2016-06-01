package com.ayondo.academy.fragments.settings;

import android.os.Bundle;
import android.support.v4.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.ayondo.academy.fragments.DashboardNavigator;
import com.ayondo.academy.fragments.base.ActionBarOwnerMixin;
import dagger.Lazy;
import javax.inject.Inject;
import rx.internal.util.SubscriptionList;

public class BasePreferenceFragment extends PreferenceFragment
{
    @Inject Lazy<DashboardNavigator> navigator;
    protected ActionBarOwnerMixin actionBarOwnerMixin;
    protected SubscriptionList onStopSubscriptions;

    @Override public void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        actionBarOwnerMixin = ActionBarOwnerMixin.of(this);
    }

    @Override public void onStart()
    {
        super.onStart();
        this.onStopSubscriptions = new SubscriptionList();
    }

    @Override public void onStop()
    {
        this.onStopSubscriptions.unsubscribe();
        super.onStop();
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
                    return true;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Should inject navigator instead of using this method
    @Deprecated
    public DashboardNavigator getNavigator()
    {
        return navigator.get();
    }
}
