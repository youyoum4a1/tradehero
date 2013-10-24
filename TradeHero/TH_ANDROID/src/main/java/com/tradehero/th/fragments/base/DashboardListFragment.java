package com.tradehero.th.fragments.base;

import android.R;
import android.os.Bundle;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;

/** Created with IntelliJ IDEA. User: tho Date: 10/24/13 Time: 10:48 AM Copyright (c) TradeHero */
public class DashboardListFragment extends BaseListFragment
{
    private DashboardNavigator navigator;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (!(getActivity() instanceof DashboardNavigatorActivity))
        {
            throw new IllegalArgumentException("DashboardActivity needs to implement DashboardNavigator");
        }

        navigator = ((DashboardNavigatorActivity) getActivity()).getDashboardNavigator();
    }

    public DashboardNavigator getNavigator()
    {
        return navigator;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.home:
                navigator.popFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
