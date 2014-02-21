package com.tradehero.th.network.service;

import com.tradehero.th.api.trade.TradeDTO;
import java.util.List;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by julien on 22/10/13
 */
public interface TradeService
{
    //<editor-fold desc="Get One Position Trades List">
    @GET("/users/{userId}/portfolios/{portfolioId}/positions/{positionId}/trades")
    List<TradeDTO> getTrades(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId,
            @Path("positionId") int positionId)
        throws RetrofitError;

    @GET("/users/{userId}/portfolios/{portfolioId}/positions/{positionId}/trades")
    void getTrades(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId,
            @Path("positionId" )int positionId,
            Callback<List<TradeDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="Get Single Trade">
    @GET("/users/{userId}/portfolios/{portfolioId}/positions/{positionId}/trades/{tradeId}")
    TradeDTO getTrade(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId,
            @Path("positionId") int positionId,
            @Path("tradeId") int tradeId)
        throws RetrofitError;

    @GET("/users/{userId}/portfolios/{portfolioId}/positions/{positionId}/trades/{tradeId}")
    void getTrade(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId,
            @Path("positionId") int positionId,
            @Path("tradeId") int tradeId,
            Callback<TradeDTO> callback);
    //</editor-fold>
}
