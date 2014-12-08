package com.tradehero.th.fragments.watchlist;

import dagger.Component;

/**
 * Created by tho on 9/9/2014.
 */
@Component
public interface FragmentWatchlistComponent
{
    void injectWatchlistPositionFragment(WatchlistPositionFragment target);
    void injectWatchlistItemView(WatchlistItemView target);
    void injectWatchlistPortfolioHeaderView(WatchlistPortfolioHeaderView target);
}
