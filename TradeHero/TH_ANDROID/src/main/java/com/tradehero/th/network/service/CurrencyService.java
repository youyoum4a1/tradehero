package com.tradehero.th.network.service;

import com.tradehero.th.api.market.CurrencyDTO;
import java.util.List;
import retrofit.RetrofitError;
import retrofit.http.GET;

interface CurrencyService
{
    //<editor-fold desc="Get Currencies">
    @GET("/currencies")
    List<CurrencyDTO> getCurrencies()
        throws RetrofitError;
    //</editor-fold>
}
