package com.ayondo.academy.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Menu;
import com.ayondo.academy.R;
import com.ayondo.academy.fragments.updatecenter.UpdateCenterFragment;

public class UpdateCenterActivity extends OneFragmentActivity
{
    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thRouter.registerRoutes(UpdateCenterFragment.class);
        thRouter.registerAlias("messages", "updatecenter/0");
        thRouter.registerAlias("notifications", "updatecenter/1");
    }

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
