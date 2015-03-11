package com.tradehero.th.network.service;

import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import retrofit.http.GET;
import retrofit.http.Query;

public interface WatchlistService
{

    //<editor-fold desc="Query for watchlist">
    @GET("/watchlistPositions")
    WatchlistPositionDTOList getAllByUser(
            @Query("pageNumber") Integer pageNumber,
            @Query("perPage") Integer perPage,
            @Query("securityId") Integer securityId,
            @Query("skipCache") Boolean skipCache);
    //</editor-fold>

}
