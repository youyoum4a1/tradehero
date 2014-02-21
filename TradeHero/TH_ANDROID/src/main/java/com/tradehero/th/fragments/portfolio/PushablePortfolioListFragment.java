package com.tradehero.th.fragments.portfolio;

/** Created with IntelliJ IDEA. User: xavier Date: 10/25/13 Time: 4:52 PM To change this template use File | Settings | File Templates. */
public class PushablePortfolioListFragment extends PortfolioListFragment
{
    public static final String TAG = PushablePortfolioListFragment.class.getSimpleName();

    @Override public boolean isDisplayHomeAsUpEnabled()
    {
        return true;
    }
}
