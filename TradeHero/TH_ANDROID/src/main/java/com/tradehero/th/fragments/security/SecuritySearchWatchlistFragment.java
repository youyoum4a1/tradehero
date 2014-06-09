package com.tradehero.th.fragments.security;

import android.os.Bundle;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.watchlist.WatchlistPositionFragment;

public class SecuritySearchWatchlistFragment extends SecuritySearchFragment
{
    @Override protected void handleSecurityClicked(SecurityCompactDTO clicked)
    {
        pushWatchlistFragmentIn(clicked.getSecurityId());
    }

    protected void pushWatchlistFragmentIn(SecurityId securityId)
    {
        Bundle args = new Bundle();
        WatchlistEditFragment.putSecurityId(args, securityId);
        args.putString(
                Navigator.BUNDLE_KEY_RETURN_FRAGMENT,
                WatchlistPositionFragment.class.getName());
        getNavigator().pushFragment(WatchlistEditFragment.class, args);
    }

}
