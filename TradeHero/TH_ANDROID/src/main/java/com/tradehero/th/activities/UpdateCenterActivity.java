package com.tradehero.th.activities;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Menu;
import com.tradehero.th.R;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;

public class UpdateCenterActivity extends OneFragmentActivity
{
    @NonNull @Override protected Class<? extends Fragment> getInitialFragment()
    {
        return UpdateCenterFragment.class;
    }

    @Override public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.update_center_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
