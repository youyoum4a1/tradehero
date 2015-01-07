package com.tradehero.th.fragments.security;

import android.content.Context;
import android.os.Bundle;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.watchlist.WatchlistPositionFragment;
import javax.inject.Inject;

public class SecuritySearchWatchlistFragment extends SecuritySearchFragment
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    @Override protected void pushTradeFragmentIn(SecurityCompactDTO securityCompactDTO)
    {
        Bundle args = new Bundle();
        WatchlistEditFragment.putSecurityId(args, securityCompactDTO.getSecurityId());
        args.putString(
                DashboardNavigator.BUNDLE_KEY_RETURN_FRAGMENT,
                WatchlistPositionFragment.class.getName());
        navigator.get().pushFragment(WatchlistEditFragment.class, args);
    }
}
