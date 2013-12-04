package com.tradehero.th.network.service;

import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionFormDTO;
import java.util.List;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/** Created with IntelliJ IDEA. User: tho Date: 12/3/13 Time: 5:46 PM Copyright (c) TradeHero */
public interface WatchlistService
{
    //<editor-fold desc="Add/Edit a watch item">
    @POST("/watchlistPositions")
    WatchlistPositionDTO createWatchlistEntry(
            WatchlistPositionFormDTO watchlistPositionFormDTO
    ) throws RetrofitError;

    @POST("/watchlistPositions")
    void createWatchlistEntry(
            WatchlistPositionFormDTO watchlistPositionFormDTO,
            Callback<WatchlistPositionDTO> callback
    );
    //</editor-fold>

    //<editor-fold desc="Query for watchlist">
    @GET("/watchlistPositions")
    List<WatchlistPositionDTO> GetAllByUser(
            @Query("pageNumber") Integer pageNumber,
            @Query("perPage") Integer perPage,
            @Query("securityId") Integer securityId,
            @Query("skipCache") Boolean skipCache)
            throws RetrofitError;

    @GET("/watchlistPositions")
    void GetAllByUser(
            @Query("pageNumber") Integer pageNumber,
            @Query("perPage") Integer perPage,
            @Query("securityId") Integer securityId,
            @Query("skipCache") Boolean skipCache,
            Callback<List<WatchlistPositionDTO>> callback);
    //</editor-fold>
}
