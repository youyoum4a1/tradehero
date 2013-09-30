package com.tradehero.th.fragments.base;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragment;
import com.tradehero.th.base.NavigationController;
import com.tradehero.th.utils.DaggerUtils;

/** Created with IntelliJ IDEA. User: tho Date: 9/27/13 Time: 5:14 PM Copyright (c) TradeHero */
public class BaseFragment extends SherlockFragment
{
    private NavigationController navigationController;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        DaggerUtils.inject(this);
    }


    protected NavigationController getNavigationController()
    {
        if (navigationController == null) {
            navigationController = new NavigationController(getSherlockActivity());
        }
        return navigationController;
    }
}
