package com.tradehero.th.models.watchlist;

import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import retrofit.Callback;
import retrofit.client.Response;

public class MiddleCallbackWatchlistUpdate extends MiddleCallback<WatchlistPositionDTO>
{
    private final WatchlistPositionCache watchlistPositionCache;

    public MiddleCallbackWatchlistUpdate(
            Callback<WatchlistPositionDTO> primaryCallback,
            WatchlistPositionCache watchlistPositionCache)
    {
        super(primaryCallback);
        this.watchlistPositionCache = watchlistPositionCache;
    }

    @Override public void success(WatchlistPositionDTO watchlistPositionDTO, Response response)
    {
        updateCache(watchlistPositionDTO);
        super.success(watchlistPositionDTO, response);
    }

    private void updateCache(WatchlistPositionDTO watchlistPositionDTO)
    {
        if (watchlistPositionCache != null)
        {
            watchlistPositionCache.put(watchlistPositionDTO.securityDTO.getSecurityId(), watchlistPositionDTO);
        }
    }
}
