package com.androidth.general.network.service;

import com.androidth.general.api.security.SecurityIntegerIdListForm;
import com.androidth.general.api.watchlist.WatchlistPositionDTO;
import com.androidth.general.api.watchlist.WatchlistPositionDTOList;
import com.androidth.general.api.watchlist.WatchlistPositionFormDTO;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface WatchlistServiceRx
{
    //<editor-fold desc="Add a watch item">
    @POST("/watchlistPositions")
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
    @POST("/batchCreateWatchlistPositions")
    Observable<WatchlistPositionDTOList> batchCreate(
            @Body SecurityIntegerIdListForm securityIntegerIds);
    //</editor-fold>

    //<editor-fold desc="Query for watchlist">
    @GET("/watchlistPositions")
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
