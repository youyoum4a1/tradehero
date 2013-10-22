package com.tradehero.th.network.service;

import com.tradehero.th.api.position.GetPositionsDTO;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/** Created with IntelliJ IDEA. User: xavier Date: 9/20/13 Time: 3:46 PM To change this template use File | Settings | File Templates. */
public interface PositionService
{
    //<editor-fold desc="Get One User Portfolio Positions List">
    @GET("/users/{userId}/portfolios/{portfolioId}/positions")
    void getPositions(
            @Path("userId") int userId,
            @Path("portfolioId")int portfolioId,
            Callback<GetPositionsDTO> callback);

    @GET("/users/{userId}/portfolios/{portfolioId}/positions")
    GetPositionsDTO getPositions(
            @Path("userId") int userId,
            @Path("portfolioId")int portfolioId)
            throws RetrofitError;

    @GET("/users/{userId}/portfolios/{portfolioId}/positions")
    void getPositions(
            @Path("userId") int userId,
            @Path("portfolioId")int portfolioId,
            @Query("pageNumber") Integer pageNumber,
            Callback<GetPositionsDTO> callback);

    @GET("/users/{userId}/portfolios/{portfolioId}/positions")
    GetPositionsDTO getPositions(
            @Path("userId") int userId,
            @Path("portfolioId")int portfolioId,
            @Query("pageNumber") Integer pageNumber)
            throws RetrofitError;

    @GET("/users/{userId}/portfolios/{portfolioId}/positions")
    void getPositions(
            @Path("userId") int userId,
            @Path("portfolioId")int portfolioId,
            @Query("pageNumber") Integer pageNumber,
            @Query("perPage") Integer perPage,
            Callback<GetPositionsDTO> callback);

    @GET("/users/{userId}/portfolios/{portfolioId}/positions")
    GetPositionsDTO getPositions(
            @Path("userId") int userId,
            @Path("portfolioId")int portfolioId,
            @Query("pageNumber") Integer pageNumber,
            @Query("perPage") Integer perPage)
            throws RetrofitError;
    //</editor-fold>
}
