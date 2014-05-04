package com.tradehero.th.models.watchlist;

import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import timber.log.Timber;

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
        if (watchlistPositionDTO != null && watchlistPositionDTO.securityDTO != null)
        {
            watchlistPositionCache.put(watchlistPositionDTO.securityDTO.getSecurityId(), watchlistPositionDTO);
        }
        else
        {
            Timber.e(new NullPointerException("watchlist or security null " + watchlistPositionDTO), "");
        }
        return watchlistPositionDTO;
    }
}
