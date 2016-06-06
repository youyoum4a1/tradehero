package com.androidth.general.network.service;

import com.androidth.general.api.market.ExchangeCompactDTOList;
import com.androidth.general.api.market.SectorCompactDTOList;
import com.androidth.general.api.market.SectorDTOList;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface MarketServiceRx
{
    //<editor-fold desc="Get Exchanges With Top Securities">
    @GET("/exchanges") Observable<ExchangeCompactDTOList> getExchanges(
            @Query("topNStocks") Integer topNStocks);
    //</editor-fold>

    //<editor-fold desc="Get Sectors">
    @GET("/sectors") Observable<SectorCompactDTOList> getSectorCompacts();

    @GET("/sectors") Observable<SectorDTOList> getSectors(
            @Query("topNStocks") int topNStocks);
    //</editor-fold>
}
