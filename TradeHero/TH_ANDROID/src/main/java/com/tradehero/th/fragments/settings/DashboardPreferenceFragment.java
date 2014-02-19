package com.tradehero.th.fragments.settings;

import android.support.v4.preference.PreferenceFragment;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;

/**
 * Created with IntelliJ IDEA. User: tho Date: 2/19/14 Time: 6:58 PM Copyright (c) TradeHero
 */
public class DashboardPreferenceFragment extends PreferenceFragment
{
    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                getNavigator().popFragment();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected DashboardNavigator getNavigator()
    {
        return ((DashboardNavigatorActivity) getActivity()).getDashboardNavigator();
    }
}
