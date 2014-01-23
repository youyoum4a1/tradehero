package com.tradehero.th.fragments.base;

import android.app.Activity;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragment;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.utils.DaggerUtils;

/** Created with IntelliJ IDEA. User: tho Date: 9/27/13 Time: 5:14 PM Copyright (c) TradeHero */
public class BaseFragment extends SherlockFragment
{
    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        DaggerUtils.inject(this);
    }

    public static interface TabBarVisibilityInformer
    {
        boolean isTabBarVisible();
    }
}
