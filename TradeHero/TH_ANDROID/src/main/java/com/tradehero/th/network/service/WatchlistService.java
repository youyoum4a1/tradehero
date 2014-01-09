package com.tradehero.th.network.service;

import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionFormDTO;
import java.util.List;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/** Created with IntelliJ IDEA. User: tho Date: 12/3/13 Time: 5:46 PM Copyright (c) TradeHero */
public interface WatchlistService
{
    //<editor-fold desc="Add/Edit a watch item">
    @POST("/watchlistPositions")
    WatchlistPositionDTO createWatchlistEntry(
            @Body WatchlistPositionFormDTO watchlistPositionFormDTO
    ) throws RetrofitError;

    @POST("/watchlistPositions")
    void createWatchlistEntry(
            @Body WatchlistPositionFormDTO watchlistPositionFormDTO,
            Callback<WatchlistPositionDTO> callback
    );

    @PUT("/watchlistPositions/{position}")
    void updateWatchlistEntry(
            @Path("position") int position,
            @Body WatchlistPositionFormDTO watchlistPositionFormDTO,
            Callback<WatchlistPositionDTO> callback
    );
    //</editor-fold>

    //<editor-fold desc="Query for watchlist">
    @GET("/watchlistPositions")
    List<WatchlistPositionDTO> getAllByUser(
            @Query("pageNumber") Integer pageNumber,
            @Query("perPage") Integer perPage,
            @Query("securityId") Integer securityId,
            @Query("skipCache") Boolean skipCache)
            throws RetrofitError;

    @GET("/watchlistPositions")
    List<WatchlistPositionDTO> getAllByUser(
            @Query("pageNumber") Integer pageNumber,
            @Query("perPage") Integer perPage)
            throws RetrofitError;

    @GET("/watchlistPositions")
    void getAllByUser(
            @Query("pageNumber") Integer pageNumber,
            @Query("perPage") Integer perPage,
            @Query("securityId") Integer securityId,
            @Query("skipCache") Boolean skipCache,
            Callback<List<WatchlistPositionDTO>> callback);

    //</editor-fold>
}
