package com.tradehero.th.fragments.settings;

import android.os.Bundle;
import android.support.v4.preference.PreferenceFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.special.residemenu.ResideMenu;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;
import dagger.Lazy;
import javax.inject.Inject;

public class DashboardPreferenceFragment extends PreferenceFragment
{
    @Inject Lazy<ResideMenu> resideMenuLazy;
    @Inject DashboardNavigator navigator;
    private ActionBarOwnerMixin actionBarOwnerMixin;

    @Override public void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);

        actionBarOwnerMixin = ActionBarOwnerMixin.of(this);
    }

    @Override public void onDestroy()
    {
        actionBarOwnerMixin.onDestroy();
        actionBarOwnerMixin = null;
        super.onDestroy();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        actionBarOwnerMixin.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                if (actionBarOwnerMixin.shouldShowHomeAsUp())
                {
                    navigator.popFragment();
                }
                else
                {
                    resideMenuLazy.get().openMenu();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public DashboardNavigator getNavigator()
    {
        return navigator;
    }
}
