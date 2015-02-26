package com.tradehero.th.network.service;

import com.tradehero.th.api.market.ExchangeCompactDTOList;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.api.market.ExchangeSectorListDTO;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface MarketServiceRx
{
    //<editor-fold desc="Get Exchanges With Top Securities">
    @GET("/exchanges") Observable<ExchangeCompactDTOList> getExchanges(
            @Query("topNStocks") Integer topNStocks);
    //</editor-fold>

    //<editor-fold desc="Get Exchange">
    @GET("/exchanges/{exchangeId}") Observable<ExchangeDTO> getExchange(
            @Path("exchangeId") int exchangeId);
    //</editor-fold>

    //<editor-fold desc="Get All Exchange And Sectors Compact">
    @Deprecated
    @GET("/allExchangesAndSectors") Observable<ExchangeSectorListDTO> getAllExchangeSectorCompact();
    //</editor-fold>
}
