package com.tradehero.th.fragments.security;

import dagger.Module;

@Module(
        injects = {
                SecuritySearchFragment.class,
                SecuritySearchWatchlistFragment.class,
                SecuritySearchProviderFragment.class,
                SecurityItemViewAdapter.class,
                SecurityItemView.class,
                SecurityCircleProgressBar.class,
                FXItemView.class,
                WarrantSecurityItemView.class,
                ChartFragment.class,
                StockInfoValueFragment.class,
                WarrantInfoValueFragment.class,
                ProviderSecurityListRxFragment.class,
                ProviderWarrantListRxFragment.class,
                WarrantCompetitionPagerFragment.class,
                StockInfoFragment.class,
                WatchlistEditFragment.class,
                SecurityActionListLinear.class,
        },
        library = true,
        complete = false
)
public class FragmentSecurityModule
{
}
