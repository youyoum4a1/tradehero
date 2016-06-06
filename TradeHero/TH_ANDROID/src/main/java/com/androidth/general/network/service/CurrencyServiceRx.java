package com.androidth.general.network.service;

import com.androidth.general.api.market.CurrencyDTO;
import java.util.List;
import retrofit.RetrofitError;
import retrofit.http.GET;
import rx.Observable;

interface CurrencyServiceRx
{
    //<editor-fold desc="Get Currencies">
    @GET("/currencies")
    Observable<List<CurrencyDTO>> getCurrencies()
        throws RetrofitError;
    //</editor-fold>
}
