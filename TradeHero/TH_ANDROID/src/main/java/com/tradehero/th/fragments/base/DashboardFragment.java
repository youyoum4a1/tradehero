package com.tradehero.th.fragments.base;

import android.os.Bundle;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.th.R;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.utils.AlertDialogUtil;
import javax.inject.Inject;
import timber.log.Timber;


abstract public class DashboardFragment extends BaseFragment
    implements BaseFragment.TabBarVisibilityInformer
{
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
            alertDialogUtil.popTutorialContent(getActivity(), ((WithTutorial) this).getTutorialLayout());
        }
        else
        {
            Timber.d("%s is not implementing WithTutorial interface, but has info menu", getClass().getName());
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
