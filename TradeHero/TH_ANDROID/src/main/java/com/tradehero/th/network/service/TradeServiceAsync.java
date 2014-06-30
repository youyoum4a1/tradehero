package com.tradehero.th.network.service;

import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.api.trade.TradeDTOList;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

interface TradeServiceAsync
{
    //<editor-fold desc="Get One Position Trades List">
    @GET("/users/{userId}/portfolios/{portfolioId}/positions/{positionId}/trades")
    void getTrades(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId,
            @Path("positionId") int positionId,
            Callback<TradeDTOList> callback);
    //</editor-fold>

    //<editor-fold desc="Get Single Trade">
    @GET("/users/{userId}/portfolios/{portfolioId}/positions/{positionId}/trades/{tradeId}")
    void getTrade(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId,
            @Path("positionId") int positionId,
            @Path("tradeId") int tradeId,
            Callback<TradeDTO> callback);
    //</editor-fold>
}
