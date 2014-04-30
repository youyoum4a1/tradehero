package com.tradehero.th.models.watchlist;

import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;

public class DTOProcessorWatchlistUpdate implements DTOProcessor<WatchlistPositionDTO>
{
    private final WatchlistPositionCache watchlistPositionCache;

    public DTOProcessorWatchlistUpdate(
            WatchlistPositionCache watchlistPositionCache)
    {
        this.watchlistPositionCache = watchlistPositionCache;
    }

    @Override public WatchlistPositionDTO process(WatchlistPositionDTO watchlistPositionDTO)
    {
        watchlistPositionCache.put(watchlistPositionDTO.securityDTO.getSecurityId(), watchlistPositionDTO);
        return watchlistPositionDTO;
    }
}
