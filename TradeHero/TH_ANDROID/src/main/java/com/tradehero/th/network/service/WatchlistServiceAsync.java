package com.tradehero.th.network.service;

import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionFormDTO;
import retrofit.Callback;
import retrofit.http.*;

interface WatchlistServiceAsync
{
    //<editor-fold desc="Add a watch item">
    @POST("/watchlistPositions")
    void createWatchlistEntry(
            @Body WatchlistPositionFormDTO watchlistPositionFormDTO,
            Callback<WatchlistPositionDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Edit a watch item">
    @PUT("/watchlistPositions/{position}")
    void updateWatchlistEntry(
            @Path("position") int position,
            @Body WatchlistPositionFormDTO watchlistPositionFormDTO,
            Callback<WatchlistPositionDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Delete Watchlist">
    @DELETE("/watchlistPositions/{watchlistId}")
    void deleteWatchlist(@Path("watchlistId") int watchlistId,
            Callback<WatchlistPositionDTO> callback);
    //</editor-fold>
}
