package com.tradehero.th.network.service;

import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.api.watchlist.WatchlistPositionFormDTO;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/** Created with IntelliJ IDEA. User: tho Date: 12/3/13 Time: 5:46 PM Copyright (c) TradeHero */
public interface WatchlistService
{
    //<editor-fold desc="Add a watch item">
    @POST("/watchlistPositions")
    WatchlistPositionDTO createWatchlistEntry(
            @Body WatchlistPositionFormDTO watchlistPositionFormDTO);

    @POST("/watchlistPositions")
    void createWatchlistEntry(
            @Body WatchlistPositionFormDTO watchlistPositionFormDTO,
            Callback<WatchlistPositionDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Edit a watch item">
    @PUT("/watchlistPositions/{position}")
    WatchlistPositionDTO updateWatchlistEntry(
            @Path("position") int position,
            @Body WatchlistPositionFormDTO watchlistPositionFormDTO);

    @PUT("/watchlistPositions/{position}")
    void updateWatchlistEntry(
            @Path("position") int position,
            @Body WatchlistPositionFormDTO watchlistPositionFormDTO,
            Callback<WatchlistPositionDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Query for watchlist">
    @GET("/watchlistPositions")
    WatchlistPositionDTOList getAllByUser();

    @GET("/watchlistPositions")
    void getAllByUser(Callback<WatchlistPositionDTOList> callback);

    @GET("/watchlistPositions")
    WatchlistPositionDTOList getAllByUser(
            @Query("pageNumber") Integer pageNumber);

    @GET("/watchlistPositions")
    void getAllByUser(
            @Query("pageNumber") Integer pageNumber,
            Callback<WatchlistPositionDTOList> callback);

    @GET("/watchlistPositions")
    WatchlistPositionDTOList getAllByUser(
            @Query("pageNumber") Integer pageNumber,
            @Query("perPage") Integer perPage);

    @GET("/watchlistPositions")
    void getAllByUser(
            @Query("pageNumber") Integer pageNumber,
            @Query("perPage") Integer perPage,
            Callback<WatchlistPositionDTOList> callback);

    @GET("/watchlistPositions")
    WatchlistPositionDTOList getAllByUser(
            @Query("pageNumber") Integer pageNumber,
            @Query("perPage") Integer perPage,
            @Query("securityId") Integer securityId);

    @GET("/watchlistPositions")
    void getAllByUser(
            @Query("pageNumber") Integer pageNumber,
            @Query("perPage") Integer perPage,
            @Query("securityId") Integer securityId,
            Callback<WatchlistPositionDTOList> callback);

    @GET("/watchlistPositions")
    WatchlistPositionDTOList getAllByUser(
            @Query("pageNumber") Integer pageNumber,
            @Query("perPage") Integer perPage,
            @Query("securityId") Integer securityId,
            @Query("skipCache") Boolean skipCache);

    @GET("/watchlistPositions")
    void getAllByUser(
            @Query("pageNumber") Integer pageNumber,
            @Query("perPage") Integer perPage,
            @Query("securityId") Integer securityId,
            @Query("skipCache") Boolean skipCache,
            Callback<WatchlistPositionDTOList> callback);
    //</editor-fold>

    //<editor-fold desc="Delete Watchlist">
    @DELETE("/watchlistPositions/{watchlistId}")
    WatchlistPositionDTO deleteWatchlist(@Path("watchlistId") int watchlistId);

    @DELETE("/watchlistPositions/{watchlistId}")
    void deleteWatchlist(@Path("watchlistId") int watchlistId, Callback<WatchlistPositionDTO> callback);
    //</editor-fold>
}
