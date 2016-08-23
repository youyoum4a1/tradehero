package com.androidth.general.network.service;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

interface QuoteServiceRx
{
    //<editor-fold desc="Get Raw Quote">
//    @GET("/securities/{exchange}/{securitySymbol}/quote")
    @GET("/quote/v2/{securityId}")
    Observable<Response> getRawQuote(
//            @Path("exchange") String exchange,
//            @Path("securitySymbol") String securitySymbol
            @Path("securityId") long securityId
    );
    //</editor-fold>
}
