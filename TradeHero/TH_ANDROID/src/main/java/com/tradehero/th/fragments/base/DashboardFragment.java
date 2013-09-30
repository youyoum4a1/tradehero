package com.tradehero.th.fragments.base;

import com.tradehero.th.R;
import com.tradehero.th.base.NavigationController;

/** Created with IntelliJ IDEA. User: tho Date: 9/30/13 Time: 6:56 PM Copyright (c) TradeHero */
public class DashboardFragment extends BaseFragment
{
    @Override protected NavigationController getNavigationController()
    {
        NavigationController nc = super.getNavigationController();
        nc.setFragmentContentId(R.id.realtabcontent);
        return nc;
    }
}
