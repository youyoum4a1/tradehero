package com.tradehero.th.fragments.base;

import android.os.Bundle;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;

/** Created with IntelliJ IDEA. User: tho Date: 9/30/13 Time: 6:56 PM Copyright (c) TradeHero */
public class DashboardFragment extends BaseFragment
{
    protected Navigator navigator;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (!(getActivity() instanceof NavigatorActivity))
        {
            throw new IllegalArgumentException("DashboardActivity need to implement Navigator");
        }

        navigator = ((NavigatorActivity)getActivity()).getNavigator();
    }
}
