package com.androidth.general.network.service;

import com.androidth.general.api.market.ExchangeCompactDTOList;
import com.androidth.general.api.market.SectorCompactDTOList;
import com.androidth.general.api.market.SectorDTOList;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface MarketServiceRx
{
    //<editor-fold desc="Get Exchanges With Top Securities">
    @GET("api/exchanges") Observable<ExchangeCompactDTOList> getExchanges(
            @Query("topNStocks") Integer topNStocks);
    //</editor-fold>

    //<editor-fold desc="Get Sectors">
    @GET("api/sectors") Observable<SectorCompactDTOList> getSectorCompacts();

    @GET("api/sectors") Observable<SectorDTOList> getSectors(
            @Query("topNStocks") int topNStocks);
    //</editor-fold>
}
