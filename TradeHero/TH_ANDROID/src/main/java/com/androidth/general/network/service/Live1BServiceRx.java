package com.androidth.general.network.service;


import com.androidth.general.api.live.LiveTradingSituationDTO;
import com.androidth.general.api.position.SecurityPositionTransactionDTO;
import com.androidth.general.api.security.TransactionFormDTO;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import rx.Observable;

public interface Live1BServiceRx {

    @GET("/liveTradingSituation")
    Observable<LiveTradingSituationDTO> getLiveTradingSituation();

    @GET("/{validateURL}")
    Observable<Boolean>validateData(
            @Path("validateURL") String validateURL
    );

    @POST("/securities/{exchange}/{securitySymbol}/buy")
    Observable<SecurityPositionTransactionDTO> buy(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            @Body() TransactionFormDTO transactionFormDTO);
}
