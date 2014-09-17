package com.tradehero.th.fragments.watchlist;

import dagger.Module;

/**
 * Created by tho on 9/9/2014.
 */
@Module(
        injects = {
                WatchlistPositionFragment.class,
                WatchlistItemView.class,
                WatchlistPortfolioHeaderView.class,
        },
        library = true,
        complete = false
)
public class FragmentWatchlistModule
{
}
