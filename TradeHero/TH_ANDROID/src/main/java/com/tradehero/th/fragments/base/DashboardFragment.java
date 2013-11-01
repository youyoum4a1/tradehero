package com.tradehero.th.fragments.base;

import android.R;
import android.os.Bundle;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.DashboardNavigator;

/** Created with IntelliJ IDEA. User: tho Date: 9/30/13 Time: 6:56 PM Copyright (c) TradeHero */
abstract public class DashboardFragment extends BaseFragment
    implements BaseFragment.TabBarVisibilityInformer
{
    protected DashboardNavigator navigator;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (!(getActivity() instanceof DashboardNavigatorActivity))
        {
            throw new IllegalArgumentException("DashboardActivity needs to implement DashboardNavigator");
        }

        navigator = ((DashboardNavigatorActivity) getActivity()).getDashboardNavigator();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.home:
                navigator.popFragment();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    protected Navigator getNavigator()
    {
        return navigator;
    }
}
