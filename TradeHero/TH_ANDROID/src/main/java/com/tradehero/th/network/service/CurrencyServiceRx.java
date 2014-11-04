package com.tradehero.th.network.service;

import com.tradehero.th.api.market.CurrencyDTO;
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
