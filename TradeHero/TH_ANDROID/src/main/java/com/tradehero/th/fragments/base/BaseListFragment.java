package com.tradehero.th.fragments.base;

import android.R;
import android.app.Activity;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.th.utils.DaggerUtils;

/** Created with IntelliJ IDEA. User: tho Date: 10/18/13 Time: 6:17 PM Copyright (c) TradeHero */
public class BaseListFragment extends SherlockListFragment
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
}
