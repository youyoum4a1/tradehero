package com.androidth.general.network.service;

import com.androidth.general.api.security.SecurityIntegerIdListForm;
import com.androidth.general.api.watchlist.WatchlistPositionDTO;
import com.androidth.general.api.watchlist.WatchlistPositionDTOList;
import com.androidth.general.api.watchlist.WatchlistPositionFormDTO;
import retrofit2.http.Body;

import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;

import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface WatchlistServiceRx
{
    //<editor-fold desc="Add a watch item">
    @POST("api/watchlistPositions")
    Observable<WatchlistPositionDTO> createWatchlistEntry(
            @Body WatchlistPositionFormDTO watchlistPositionFormDTO);
    //</editor-fold>

    //<editor-fold desc="Edit a watch item">
    @PUT("/watchlistPositions/{position}")
    Observable<WatchlistPositionDTO> updateWatchlistEntry(
            @Path("position") int position,
            @Body WatchlistPositionFormDTO watchlistPositionFormDTO);
    //</editor-fold>

    //<editor-fold desc="Batch Create Watchlist Positions">
    @POST("api/batchCreateWatchlistPositions")
    Observable<WatchlistPositionDTOList> batchCreate(
            @Body SecurityIntegerIdListForm securityIntegerIds);
    //</editor-fold>

    //<editor-fold desc="Query for watchlist">
    @GET("api/watchlistPositions")
    Observable<WatchlistPositionDTOList> getAllByUser(
            @Query("pageNumber") Integer pageNumber,
            @Query("perPage") Integer perPage,
            @Query("securityId") Integer securityId,
            @Query("skipCache") Boolean skipCache);
    //</editor-fold>

    //<editor-fold desc="Delete Watchlist">
    @DELETE("/watchlistPositions/{watchlistId}")
    Observable<WatchlistPositionDTO> deleteWatchlist(@Path("watchlistId") int watchlistId);
    //</editor-fold>
}
