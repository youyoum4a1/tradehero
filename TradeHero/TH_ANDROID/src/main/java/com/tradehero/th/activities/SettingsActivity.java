package com.ayondo.academy.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Menu;
import com.ayondo.academy.R;
import com.ayondo.academy.fragments.settings.SettingsFragment;

public class SettingsActivity extends OneFragmentActivity
{
    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thRouter.registerRoutes(SettingsFragment.class);
    }

    @NonNull @Override protected Class<? extends Fragment> getInitialFragment()
    {
        return SettingsFragment.class;
    }

    @Override public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
