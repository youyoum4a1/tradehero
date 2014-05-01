package com.tradehero.th.persistence.watchlist;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class WatchlistPositionCache extends StraightDTOCache<SecurityId, WatchlistPositionDTO>
{
    private static final int DEFAULT_MAX_SIZE = 200;

    @Inject public WatchlistPositionCache()
    {
        super(DEFAULT_MAX_SIZE);
    }

    @Override protected WatchlistPositionDTO fetch(SecurityId key) throws Throwable
    {
        throw new IllegalStateException("There is no fetch on WatchlistPositionCache");
    }
}
