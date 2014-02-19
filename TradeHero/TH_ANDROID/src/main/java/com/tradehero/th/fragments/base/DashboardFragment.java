package com.tradehero.th.fragments.base;

import android.os.Bundle;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.utils.AlertDialogUtil;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 9/30/13 Time: 6:56 PM Copyright (c) TradeHero */
abstract public class DashboardFragment extends BaseFragment
    implements BaseFragment.TabBarVisibilityInformer
{
    private static final String TAG = DashboardFragment.class.getName();
    @Inject AlertDialogUtil alertDialogUtil;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (!(getActivity() instanceof DashboardNavigatorActivity))
        {
            throw new IllegalArgumentException("DashboardActivity needs to implement DashboardNavigator");
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        if (this instanceof WithTutorial)
        {
            inflater.inflate(R.menu.menu_with_tutorial, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                getNavigator().popFragment();
                return true;

            case R.id.menu_info:
                handleInfoMenuItemClicked();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void handleInfoMenuItemClicked()
    {
        if (this instanceof WithTutorial)
        {
            alertDialogUtil.popCustom(getActivity(), ((WithTutorial) this).getTutorialLayout());
        }
        else
        {
            THLog.d(TAG, getClass().getName() + " is not implementing WithTutorial interface, but has info menu");
        }
    }

    protected Navigator getNavigator()
    {
        return ((NavigatorActivity) getActivity()).getNavigator();
    }

    protected DashboardNavigator getDashboardNavigator()
    {
        return ((DashboardNavigatorActivity) getActivity()).getDashboardNavigator();
    }
}
