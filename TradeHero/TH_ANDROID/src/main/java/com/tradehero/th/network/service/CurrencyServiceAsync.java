package com.tradehero.th.network.service;

import com.tradehero.th.api.market.CurrencyDTO;
import java.util.List;
import retrofit.Callback;
import retrofit.http.GET;

interface CurrencyServiceAsync
{
    //<editor-fold desc="Get Currencies">
    @GET("/currencies")
    void getCurrencies(
            Callback<List<CurrencyDTO>> callback);
    //</editor-fold>
}
