package com.androidth.general.network.service;

import com.androidth.general.api.position.GetPositionsDTO;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

interface PositionServiceRx
{
    //<editor-fold desc="Get One User Portfolio Positions List">
    @GET("api/users/{userId}/portfolios/{portfolioId}/positions")
    Observable<GetPositionsDTO> getPositions(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId,
            @Query("pageNumber") Integer pageNumber,
            @Query("perPage") Integer perPage);
    //</editor-fold>
}
