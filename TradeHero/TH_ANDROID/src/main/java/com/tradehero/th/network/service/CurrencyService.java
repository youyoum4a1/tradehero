package com.tradehero.th.network.service;

import com.tradehero.th.api.market.CurrencyDTO;
import java.util.List;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.GET;


public interface CurrencyService
{
    //<editor-fold desc="Get Currencies">
    @GET("/currencies")
    List<CurrencyDTO> getCurrencies()
        throws RetrofitError;

    @GET("/currencies")
    void getCurrencies(
            Callback<List<CurrencyDTO>> callback);
    //</editor-fold>
}
