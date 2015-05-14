package com.tradehero.th.fragments.settings;

import android.os.Bundle;
import android.support.v4.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.tradehero.th.R;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import org.jetbrains.annotations.NotNull;

public class DashboardPreferenceFragment extends PreferenceFragment {
    private static final String BUNDLE_KEY_SHOW_HOME_AS_UP = DashboardPreferenceFragment.class.getName() + ".show_home_as_up";

    private static final boolean DEFAULT_SHOW_HOME_AS_UP = true;


    public static void putKeyShowHomeAsUp(@NotNull Bundle args, @NotNull Boolean showAsUp) {
        args.putBoolean(BUNDLE_KEY_SHOW_HOME_AS_UP, showAsUp);
    }

    protected static boolean getKeyShowHomeAsUp(Bundle args) {
        if (args == null) {
            return DEFAULT_SHOW_HOME_AS_UP;
        }
        return args.getBoolean(BUNDLE_KEY_SHOW_HOME_AS_UP, DEFAULT_SHOW_HOME_AS_UP);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (shouldShowHomeAsUp()) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                    | ActionBar.DISPLAY_SHOW_TITLE
                    | ActionBar.DISPLAY_SHOW_HOME);
        } else {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE
                    | ActionBar.DISPLAY_SHOW_HOME
                    | ActionBar.DISPLAY_USE_LOGO);
            actionBar.setLogo(R.drawable.launcher);
        }
        actionBar.setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (shouldShowHomeAsUp()) {
                    getNavigator().popFragment();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected boolean shouldShowHomeAsUp() {
        return getKeyShowHomeAsUp(getArguments());
    }

    protected DashboardNavigator getNavigator() {
        return ((DashboardNavigatorActivity) getActivity()).getDashboardNavigator();
    }
}
