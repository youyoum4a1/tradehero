package com.tradehero.th.network.service;

import com.tradehero.th.api.market.ExchangeCompactDTOList;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.api.market.ExchangeSectorListDTO;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

public interface MarketServiceRx
{
    //<editor-fold desc="GetExchanges">
    // returns basic exchange DTOs, un-enriched
    @GET("/exchanges") Observable<ExchangeCompactDTOList> getExchanges();
    //</editor-fold>

    //<editor-fold desc="GetExchange">
    // returns enriched exchange DTOs: sector, industry and full stock lists
    @GET("/exchanges/{exchangeId}")
    Observable<ExchangeDTO> getExchange(
            @Path("exchangeId") int exchangeId);
    //</editor-fold>

    //<editor-fold desc="Get All Exchange And Sectors Compact">
    @GET("/allExchangesAndSectors")
    Observable<ExchangeSectorListDTO> getAllExchangeSectorCompact();
    //</editor-fold>
}
