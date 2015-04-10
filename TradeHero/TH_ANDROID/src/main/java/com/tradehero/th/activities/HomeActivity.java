package com.tradehero.th.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Menu;
import com.tradehero.th.R;
import com.tradehero.th.fragments.home.HomeFragment;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;
import com.tradehero.th.fragments.web.WebViewFragment;

public class HomeActivity extends OneFragmentActivity
{
    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thRouter.registerRoutes(
                FriendsInvitationFragment.class,
                HomeFragment.class,
                SettingsFragment.class,
                UpdateCenterFragment.class,
                WebViewFragment.class);
        thRouter.registerAlias("messages", "updatecenter/0");
        thRouter.registerAlias("notifications", "updatecenter/1");
    }

    @NonNull @Override protected Class<? extends Fragment> getInitialFragment()
    {
        return HomeFragment.class;
    }

    @Override public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }
}
