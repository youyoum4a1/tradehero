package com.tradehero.th.fragments.security;

import dagger.Module;

/**
 * Created by tho on 9/9/2014.
 */
@Module(
        injects = {
                SecuritySearchFragment.class,
                SecuritySearchWatchlistFragment.class,
                SecuritySearchProviderFragment.class,
                SecurityItemViewAdapter.class,
                SecurityItemView.class,
                FXItemView.class,
                WarrantSecurityItemView.class,
                ChartFragment.class,
                StockInfoValueFragment.class,
                WarrantInfoValueFragment.class,
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
