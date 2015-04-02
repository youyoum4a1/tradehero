package com.tradehero.th.activities;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Menu;
import com.tradehero.th.R;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;

public class FriendsInvitationActivity extends OneFragmentActivity
{
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
