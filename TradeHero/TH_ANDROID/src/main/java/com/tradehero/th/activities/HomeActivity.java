package com.tradehero.th.activities;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Menu;
import com.tradehero.th.R;
import com.tradehero.th.fragments.home.HomeFragment;

public class HomeActivity extends OneFragmentActivity
{
    @NonNull @Override protected Class<? extends Fragment> getInitialFragment()
    {
        return HomeFragment.class;
    }

    @Override public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
