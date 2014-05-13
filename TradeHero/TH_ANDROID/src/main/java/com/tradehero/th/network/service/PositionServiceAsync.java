package com.tradehero.th.network.service;

import com.tradehero.th.api.position.GetPositionsDTO;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

interface PositionServiceAsync
{
    //<editor-fold desc="Get One User Portfolio Positions List">
    @GET("/users/{userId}/portfolios/{portfolioId}/positions")
    void getPositions(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId,
            @Query("pageNumber") Integer pageNumber,
            @Query("perPage") Integer perPage,
            Callback<GetPositionsDTO> callback);
    //</editor-fold>
}
