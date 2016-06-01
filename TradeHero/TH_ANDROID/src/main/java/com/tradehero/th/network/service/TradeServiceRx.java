package com.ayondo.academy.network.service;

import com.ayondo.academy.api.trade.TradeDTO;
import com.ayondo.academy.api.trade.TradeDTOList;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

public interface TradeServiceRx
{
    //<editor-fold desc="Get One Position Trades List">
    @GET("/users/{userId}/portfolios/{portfolioId}/positions/{positionId}/trades")
    Observable<TradeDTOList> getTrades(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId,
            @Path("positionId") int positionId);
    //</editor-fold>

    //<editor-fold desc="Get Single Trade">
    @GET("/users/{userId}/portfolios/{portfolioId}/positions/{positionId}/trades/{tradeId}")
    Observable<TradeDTO> getTrade(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId,
            @Path("positionId") int positionId,
            @Path("tradeId") int tradeId);
    //</editor-fold>
}
