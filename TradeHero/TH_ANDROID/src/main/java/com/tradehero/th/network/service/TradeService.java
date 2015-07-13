package com.tradehero.th.network.service;

import com.tradehero.th.api.trade.ClosedTradeDTOList;
import com.tradehero.th.api.trade.TradeDTOList;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

public interface TradeService
{
    //<editor-fold desc="Get One Position Trades List">
    @GET("/users/{userId}/portfolios/{portfolioId}/positions/{positionId}/trades")
    TradeDTOList getTrades(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId,
            @Path("positionId") int positionId);
    //</editor-fold>

    @GET("/cn/v2/trades")//查询成交
    void getClosedTrade(Callback<ClosedTradeDTOList> callback);

    @GET("/cn/v2/orders")//查询委托
    void getDelegation(Callback<ClosedTradeDTOList> callback);

    @GET("/cn/v2/orders/pending")//可撤单委托
    void getPendingDelegation(Callback<ClosedTradeDTOList> callback);
}
