package com.tradehero.th.fragments.security;

import android.content.Context;
import android.os.Bundle;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.trending.TrendingMainFragment;
import com.tradehero.th.fragments.watchlist.WatchlistPositionFragment;
import javax.inject.Inject;

public class SecuritySearchWatchlistFragment extends SecuritySearchFragment
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    private static final String BUNDLE_KEY_RETURN_FRAGMENT = "SecuritySearchWatchlistFragment.returnFragment";
    public static void putReturnFragment(Bundle args, String returnFragment) {
        args.putString(BUNDLE_KEY_RETURN_FRAGMENT, returnFragment);
    }

    private String getReturnFragment() {
        Bundle args = getArguments();
        if (args == null) {
            return TrendingMainFragment.class.getName();
        }

        return args.getString(BUNDLE_KEY_RETURN_FRAGMENT, TrendingMainFragment.class.getName());
    }

    @Override protected void pushTradeFragmentIn(SecurityCompactDTO securityCompactDTO)
    {
        Bundle args = new Bundle();
        String returnFragment = getReturnFragment();
        WatchlistEditFragment.putSecurityId(args, securityCompactDTO.getSecurityId());
        args.putString(
                DashboardNavigator.BUNDLE_KEY_RETURN_FRAGMENT, returnFragment);
        navigator.get().pushFragment(WatchlistEditFragment.class, args);
    }
}
