package com.tradehero.th.persistence.watchlist;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: tho Date: 12/3/13 Time: 5:54 PM Copyright (c) TradeHero */
@Singleton public class WatchlistPositionCache extends StraightDTOCache<WatchlistPositionKey, WatchlistPositionDTO>
{
    private static final int DEFAULT_MAX_SIZE = 200;

    @Inject public WatchlistPositionCache()
    {
        super(DEFAULT_MAX_SIZE);
    }

    @Override protected WatchlistPositionDTO fetch(WatchlistPositionKey key) throws Throwable
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
