package com.tradehero.th.fragments.portfolio;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

/** Created with IntelliJ IDEA. User: xavier Date: 10/25/13 Time: 4:52 PM To change this template use File | Settings | File Templates. */
public class PushablePortfolioListFragment extends PortfolioListFragment
{
    public static final String TAG = PushablePortfolioListFragment.class.getSimpleName();

    @Override public boolean isDisplayHomeAsUpEnabled()
    {
        return true;
    }
}
