package com.tradehero.th.network.service;

import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionFormDTO;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.POST;

/** Created with IntelliJ IDEA. User: tho Date: 12/3/13 Time: 5:46 PM Copyright (c) TradeHero */
public interface WatchlistService
{
    @POST("/watchlistPositions")
    WatchlistPositionDTO createWatchlistEntry(WatchlistPositionFormDTO watchlistPositionFormDTO) throws RetrofitError;

    @POST("/watchlistPositions")
    void createWatchlistEntry(WatchlistPositionFormDTO watchlistPositionFormDTO, Callback<WatchlistPositionDTO> callback);
}
