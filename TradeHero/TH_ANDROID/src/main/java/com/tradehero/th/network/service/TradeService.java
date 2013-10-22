package com.tradehero.th.network.service;

import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.trade.TradeDTO;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.GET;
import retrofit.http.Path;

import java.util.List;

/**
 * Created by julien on 22/10/13
 */
public interface TradeService
{
    @GET("/users/{userId}/portfolios/{portfolioId}/positions/{positionId}/trades")
    void getTrades(
            @Path("userId") int userId,
            @Path("portfolioId")int portfolioId,
            @Path("positionId")int positionId,
            Callback<List<TradeDTO>> callback);

    @GET("/users/{userId}/portfolios/{portfolioId}/positions/{positionId}/trades")
    List<TradeDTO> getTrades(
            @Path("userId") int userId,
            @Path("portfolioId")int portfolioId,
            @Path("positionId")int positionId)
            throws RetrofitError;

}
