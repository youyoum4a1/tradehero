package com.tradehero.chinabuild.data;

import com.tradehero.th.api.watchlist.WatchlistPositionDTO;

/**
 * Created by huhaiping on 14-8-26.
 */
public class WatchPositionItem implements PositionInterface
{
    public WatchlistPositionDTO watchlistPosition;

    public WatchPositionItem(WatchlistPositionDTO watchlistPosition)
    {
        this.watchlistPosition = watchlistPosition;
    }
}
