package com.tradehero.th.network.service;

import com.tradehero.th.api.position.GetPositionsDTO;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface PositionService
{
    //<editor-fold desc="Get One User Portfolio Positions List">
    @GET("/users/{userId}/portfolios/{portfolioId}/positions")
    GetPositionsDTO getPositions(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId,
            @Query("pageNumber") Integer pageNumber,
            @Query("perPage") Integer perPage);
    //</editor-fold>
}
