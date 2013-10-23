package com.tradehero.th.network.service;

import com.tradehero.th.api.market.ExchangeDTO;
import java.util.List;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.GET;
import retrofit.http.Path;

/** Created with IntelliJ IDEA. User: xavier Date: 10/18/13 Time: 5:58 PM To change this template use File | Settings | File Templates. */
public interface MarketService
{
    //<editor-fold desc="GetExchanges">
    // returns basic exchange DTOs, un-enriched
    @GET("/exchanges")
    List<ExchangeDTO> getExchanges()
        throws RetrofitError;

    // returns basic exchange DTOs, un-enriched
    @GET("/exchanges")
    void getExchanges(
            Callback<List<ExchangeDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="GetExchange">
    // returns enriched exchange DTOs: sector, industry and full stock lists
    @GET("/exchanges/{exchangeId}")
    ExchangeDTO getExchange(
            @Path("exchangeId") int exchangeId)
        throws RetrofitError;

    // returns enriched exchange DTOs: sector, industry and full stock lists
    @GET("/exchanges/{exchangeId}")
    void getExchange(
            @Path("exchangeId") int exchangeId,
            Callback<ExchangeDTO> callback);
    //</editor-fold>
}
