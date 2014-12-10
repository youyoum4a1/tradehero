package com.tradehero.th.network.service;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;

interface QuoteServiceRx
{
    //<editor-fold desc="Get Raw Quote">
    // We should not ask Observable<Response> from Retrofit as Response is not a json convertible object
    @GET("/securities/{exchange}/{securitySymbol}/quote")
    Response getRawQuote(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol);

    @GET("/FX/{fxSymbol}/quote")
    Response getRawFxQuote(
            @Path("fxSymbol") String fxSymbol);
    //</editor-fold>
}
