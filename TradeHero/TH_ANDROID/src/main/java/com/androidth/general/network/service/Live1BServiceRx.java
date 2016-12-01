package com.androidth.general.network.service;


import com.androidth.general.api.live.LiveTradingSituationDTO;
import com.androidth.general.api.position.SecurityPositionTransactionDTO;
import com.androidth.general.api.security.TransactionFormDTO;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface Live1BServiceRx {

    @GET("api/liveTradingSituation")
    Observable<LiveTradingSituationDTO> getLiveTradingSituation();

    @GET("api/{validateURL}")
    Observable<Boolean>validateData(
            @Path("validateURL") String validateURL
    );

    @POST("api/securities/{exchange}/{securitySymbol}/buy")
    Observable<SecurityPositionTransactionDTO> buy(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            @Body() TransactionFormDTO transactionFormDTO);
}
