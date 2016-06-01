package com.ayondo.academy.fragments.security;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import com.ayondo.academy.api.security.SecurityCompactDTO;
import com.ayondo.academy.fragments.DashboardNavigator;
import com.ayondo.academy.fragments.trending.TrendingMainFragment;
import javax.inject.Inject;

public class SecuritySearchWatchlistFragment extends SecuritySearchFragment
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    private static final String BUNDLE_KEY_RETURN_FRAGMENT = "SecuritySearchWatchlistFragment.returnFragment";

    public static void putReturnFragment(@NonNull Bundle args, @NonNull Class<? extends Fragment> returnFragment)
    {
        args.putString(BUNDLE_KEY_RETURN_FRAGMENT, returnFragment.getName());
    }

    @NonNull private String getReturnFragment()
    {
        Bundle args = getArguments();
        if (args == null)
        {
            return TrendingMainFragment.class.getName();
        }

        return args.getString(BUNDLE_KEY_RETURN_FRAGMENT, TrendingMainFragment.class.getName());
    }

    @Override protected void pushTradeFragmentIn(SecurityCompactDTO securityCompactDTO)
    {
        Bundle args = new Bundle();
        String returnFragment = getReturnFragment();
        WatchlistEditFragment.putSecurityId(args, securityCompactDTO.getSecurityId());
        DashboardNavigator.putReturnFragment(args, returnFragment);
        navigator.get().pushFragment(WatchlistEditFragment.class, args);
    }
}
