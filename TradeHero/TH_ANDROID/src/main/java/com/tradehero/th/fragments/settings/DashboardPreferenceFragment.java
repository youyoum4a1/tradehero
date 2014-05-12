package com.tradehero.th.fragments.settings;

import android.support.v4.preference.PreferenceFragment;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;

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

    protected Navigator getNavigator()
    {
        return ((NavigatorActivity) getActivity()).getNavigator();
    }
}
