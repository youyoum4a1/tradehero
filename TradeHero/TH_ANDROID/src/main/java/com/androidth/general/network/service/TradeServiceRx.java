package com.androidth.general.network.service;

import com.androidth.general.api.trade.TradeDTO;
import com.androidth.general.api.trade.TradeDTOList;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface TradeServiceRx
{
    //<editor-fold desc="Get One Position Trades List">
    @GET("api/users/{userId}/portfolios/{portfolioId}/positions/{positionId}/trades")
    Observable<TradeDTOList> getTrades(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId,
            @Path("positionId") int positionId);
    //</editor-fold>

    //<editor-fold desc="Get Single Trade">
    @GET("api/users/{userId}/portfolios/{portfolioId}/positions/{positionId}/trades/{tradeId}")
    Observable<TradeDTO> getTrade(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId,
            @Path("positionId") int positionId,
            @Path("tradeId") int tradeId);
    //</editor-fold>
}
