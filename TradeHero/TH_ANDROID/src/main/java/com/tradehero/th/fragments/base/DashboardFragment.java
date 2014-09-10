package com.tradehero.th.fragments.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.special.residemenu.ResideMenu;
import com.tradehero.th.R;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.utils.AlertDialogUtil;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

abstract public class DashboardFragment extends BaseFragment
{
    @Inject protected AlertDialogUtil alertDialogUtil;

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
        super.onCreateOptionsMenu(menu, inflater);
        if (this instanceof WithTutorial)
        {
            inflater.inflate(R.menu.menu_with_tutorial, menu);
        }

        actionBarOwnerMixin.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                if (actionBarOwnerMixin.shouldShowHomeAsUp())
                {
                    DashboardNavigator navigator = getDashboardNavigator();
                    if (navigator != null)
                    {
                        navigator.popFragment();
                    }
                }
                else
                {
                    //resideMenuLazy.get().openMenu();
                }
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

    @Nullable protected DashboardNavigator getDashboardNavigator()
    {
        @Nullable DashboardNavigatorActivity activity = ((DashboardNavigatorActivity) getActivity());
        if (activity != null)
        {
            return activity.getDashboardNavigator();
        }
        return null;
    }

    public <T extends Fragment> boolean allowNavigateTo(@NotNull Class<T> fragmentClass, Bundle args)
    {
        return true;
    }
}
