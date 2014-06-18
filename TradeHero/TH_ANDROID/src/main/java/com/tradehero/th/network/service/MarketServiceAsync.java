package com.tradehero.th.network.service;

import com.tradehero.th.api.market.ExchangeCompactDTOList;
import com.tradehero.th.api.market.ExchangeDTO;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

public interface MarketServiceAsync
{
    //<editor-fold desc="GetExchanges">
    // returns basic exchange DTOs, un-enriched
    @GET("/exchanges")
    void getExchanges(
            Callback<ExchangeCompactDTOList> callback);
    //</editor-fold>

    //<editor-fold desc="GetExchange">
    // returns enriched exchange DTOs: sector, industry and full stock lists
    @GET("/exchanges/{exchangeId}")
    void getExchange(
            @Path("exchangeId") int exchangeId,
            Callback<ExchangeDTO> callback);
    //</editor-fold>
}
