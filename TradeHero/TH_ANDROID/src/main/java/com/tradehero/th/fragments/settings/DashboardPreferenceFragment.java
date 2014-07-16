package com.tradehero.th.fragments.settings;

import android.os.Bundle;
import android.support.v4.preference.PreferenceFragment;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.special.residemenu.ResideMenu;
import com.tradehero.th.R;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class DashboardPreferenceFragment extends PreferenceFragment
{
    private static final String BUNDLE_KEY_SHOW_HOME_AS_UP = DashboardPreferenceFragment.class.getName() + ".show_home_as_up";

    private static final boolean DEFAULT_SHOW_HOME_AS_UP = true;

    @Inject Lazy<ResideMenu> resideMenuLazy;

    public static void putKeyShowHomeAsUp(@NotNull Bundle args, @NotNull Boolean showAsUp)
    {
        args.putBoolean(BUNDLE_KEY_SHOW_HOME_AS_UP, showAsUp);
    }

    protected static boolean getKeyShowHomeAsUp(Bundle args)
    {
        if (args == null)
        {
            return DEFAULT_SHOW_HOME_AS_UP;
        }
        return args.getBoolean(BUNDLE_KEY_SHOW_HOME_AS_UP, DEFAULT_SHOW_HOME_AS_UP);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        if (shouldShowHomeAsUp())
        {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                    | ActionBar.DISPLAY_SHOW_TITLE
                    | ActionBar.DISPLAY_SHOW_HOME);
        }
        else
        {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE
                    | ActionBar.DISPLAY_SHOW_HOME
                    | ActionBar.DISPLAY_USE_LOGO);
            actionBar.setLogo(R.drawable.icn_actionbar_hamburger);
        }
        actionBar.setHomeButtonEnabled(true);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                if (shouldShowHomeAsUp())
                {
                    getNavigator().popFragment();
                }
                else
                {
                    resideMenuLazy.get().openMenu();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected boolean shouldShowHomeAsUp()
    {
        return getKeyShowHomeAsUp(getArguments());
    }

    protected Navigator getNavigator()
    {
        return ((NavigatorActivity) getActivity()).getNavigator();
    }
}
