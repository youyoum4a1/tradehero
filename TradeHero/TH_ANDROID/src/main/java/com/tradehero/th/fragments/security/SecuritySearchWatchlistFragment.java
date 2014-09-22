package com.tradehero.th.fragments.security;

import android.content.Context;
import android.os.Bundle;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.watchlist.WatchlistPositionFragment;
import javax.inject.Inject;

public class SecuritySearchWatchlistFragment extends SecuritySearchFragment
{
    @Inject Context doNotRemoveOtherwiseFails; // Do not remove otherwise fails

    @Override protected void pushTradeFragmentIn(SecurityId securityId)
    {
        Bundle args = new Bundle();
        WatchlistEditFragment.putSecurityId(args, securityId);
        args.putString(
                DashboardNavigator.BUNDLE_KEY_RETURN_FRAGMENT,
                WatchlistPositionFragment.class.getName());
        navigator.pushFragment(WatchlistEditFragment.class, args);
    }
}
