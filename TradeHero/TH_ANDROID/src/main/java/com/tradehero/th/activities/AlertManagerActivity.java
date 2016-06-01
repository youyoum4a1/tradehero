package com.ayondo.academy.activities;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Menu;
import com.ayondo.academy.R;
import com.ayondo.academy.fragments.alert.AlertManagerFragment;

public class AlertManagerActivity extends OneFragmentActivity
{
    @NonNull @Override protected Class<? extends Fragment> getInitialFragment()
    {
        return AlertManagerFragment.class;
    }

    @Override public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.alert_manager_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
