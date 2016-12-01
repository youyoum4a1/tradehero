package com.androidth.general.network.service;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

interface QuoteServiceRx
{
    //<editor-fold desc="Get Raw Quote">
//    @GET("api/securities/{exchange}/{securitySymbol}/quote")
    @GET("api/quote/v2/{securityId}")
    Observable<Response> getRawQuote(
//            @Path("exchange") String exchange,
//            @Path("securitySymbol") String securitySymbol
            @Path("securityId") long securityId
    );
    //</editor-fold>
}
