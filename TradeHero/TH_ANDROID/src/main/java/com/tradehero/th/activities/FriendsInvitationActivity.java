package com.ayondo.academy.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Menu;
import com.ayondo.academy.R;
import com.ayondo.academy.fragments.social.friend.FriendsInvitationFragment;
import com.ayondo.academy.utils.DeviceUtil;

public class FriendsInvitationActivity extends OneFragmentActivity
{
    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thRouter.registerRoutes(FriendsInvitationFragment.class);
        DeviceUtil.dismissKeyboard(this);
    }

    @NonNull @Override protected Class<? extends Fragment> getInitialFragment()
    {
        return FriendsInvitationFragment.class;
    }

    @Override public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.friends_invitation_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
