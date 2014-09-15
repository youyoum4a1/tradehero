package com.tradehero.th.fragments.base;

import android.os.Bundle;

import com.actionbarsherlock.view.MenuItem;
import com.tradehero.th.fragments.DashboardNavigator;
import javax.inject.Inject;

public class DashboardListFragment extends BaseListFragment
{
    @Inject DashboardNavigator navigator;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    public DashboardNavigator getNavigator()
    {
        return navigator;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                navigator.popFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
