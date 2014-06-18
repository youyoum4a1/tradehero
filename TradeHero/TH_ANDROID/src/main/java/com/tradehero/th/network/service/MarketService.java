package com.tradehero.th.network.service;

import com.tradehero.th.api.market.ExchangeCompactDTOList;
import com.tradehero.th.api.market.ExchangeDTO;
import retrofit.http.GET;
import retrofit.http.Path;

public interface MarketService
{
    //<editor-fold desc="GetExchanges">
    // returns basic exchange DTOs, un-enriched
    @GET("/exchanges") ExchangeCompactDTOList getExchanges();
    //</editor-fold>

    //<editor-fold desc="GetExchange">
    // returns enriched exchange DTOs: sector, industry and full stock lists
    @GET("/exchanges/{exchangeId}")
    ExchangeDTO getExchange(
            @Path("exchangeId") int exchangeId);
    //</editor-fold>
}
