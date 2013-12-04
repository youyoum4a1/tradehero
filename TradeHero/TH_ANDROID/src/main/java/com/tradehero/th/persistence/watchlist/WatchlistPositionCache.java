package com.tradehero.th.persistence.watchlist;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.network.service.WatchlistService;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: tho Date: 12/3/13 Time: 5:54 PM Copyright (c) TradeHero */
@Singleton public class WatchlistPositionCache extends StraightDTOCache<WatchlistPositionId, WatchlistPositionDTO>
{
    private static final int DEFAULT_MAX_SIZE = 200;

    @Inject protected Lazy<WatchlistService> watchlistService;

    @Inject public WatchlistPositionCache()
    {
        super(DEFAULT_MAX_SIZE);
    }

    @Override protected WatchlistPositionDTO fetch(WatchlistPositionId key) throws Throwable
    {
        //watchlistService.get().
        return null;
    }
}
