package com.tradehero.th.network.service;

import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.api.watchlist.WatchlistPositionFormDTO;
import retrofit.http.*;

public interface WatchlistService
{

    //<editor-fold desc="Edit a watch item">
    @PUT("/watchlistPositions/{position}")
    WatchlistPositionDTO updateWatchlistEntry(
            @Path("position") int position,
            @Body WatchlistPositionFormDTO watchlistPositionFormDTO);
    //</editor-fold>

    //<editor-fold desc="Query for watchlist">
    @GET("/watchlistPositions")
    WatchlistPositionDTOList getAllByUser(
            @Query("pageNumber") Integer pageNumber,
            @Query("perPage") Integer perPage,
            @Query("securityId") Integer securityId,
            @Query("skipCache") Boolean skipCache);
    //</editor-fold>

    //<editor-fold desc="Delete Watchlist">
    @DELETE("/watchlistPositions/{watchlistId}")
    WatchlistPositionDTO deleteWatchlist(@Path("watchlistId") int watchlistId);
    //</editor-fold>
}
