package com.ayondo.academy.network.service;

import com.ayondo.academy.api.position.GetPositionsDTO;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

interface PositionServiceRx
{
    //<editor-fold desc="Get One User Portfolio Positions List">
    @GET("/users/{userId}/portfolios/{portfolioId}/positions")
    Observable<GetPositionsDTO> getPositions(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId,
            @Query("pageNumber") Integer pageNumber,
            @Query("perPage") Integer perPage);
    //</editor-fold>
}
