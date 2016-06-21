package com.androidth.general.fragments.security;

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
                ProviderSecurityListRxFragment.class,
                ProviderSecurityV2RxFragment.class,
                ProviderWarrantListRxFragment.class,
                WarrantCompetitionPagerFragment.class,
                WatchlistEditFragment.class,
        },
        library = true,
        complete = false
)
public class FragmentSecurityModule
{
}
