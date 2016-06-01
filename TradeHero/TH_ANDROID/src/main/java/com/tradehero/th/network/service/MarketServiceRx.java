package com.ayondo.academy.network.service;

import com.ayondo.academy.api.market.ExchangeCompactDTOList;
import com.ayondo.academy.api.market.SectorCompactDTOList;
import com.ayondo.academy.api.market.SectorDTOList;
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
