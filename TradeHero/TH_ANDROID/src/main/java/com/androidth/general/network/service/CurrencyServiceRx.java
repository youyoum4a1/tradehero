package com.androidth.general.network.service;

import com.androidth.general.api.market.CurrencyDTO;
import com.androidth.general.models.retrofit2.THRetrofitException;

import java.util.List;
import retrofit2.http.GET;
import rx.Observable;

interface CurrencyServiceRx
{
    //<editor-fold desc="Get Currencies">
    @GET("api/currencies")
    Observable<List<CurrencyDTO>> getCurrencies()
        throws THRetrofitException;
    //</editor-fold>
}
