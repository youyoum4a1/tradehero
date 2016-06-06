package com.androidth.general.activities;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Menu;
import com.androidth.general.R;
import com.androidth.general.fragments.settings.AdminSettingsFragment;

public class AdminSettingsActivity extends OneFragmentActivity
{
    @NonNull @Override protected Class<? extends Fragment> getInitialFragment()
    {
        return AdminSettingsFragment.class;
    }

    @Override public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.admin_settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
