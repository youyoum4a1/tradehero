package com.androidth.general.network.service;

import com.androidth.general.api.SignatureContainer;
import com.androidth.general.fragments.security.LiveQuoteDTO;
import com.androidth.general.fragments.security.LiveSignatureContainer;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

interface QuoteServiceRx
{
    //<editor-fold desc="Get Raw Quote">
//    @GET("api/securities/{exchange}/{securitySymbol}/quote")
    @GET("api/quote/v2/{securityId}")
    Observable<Response<ResponseBody>> getRawQuote(
//            @Path("exchange") String exchange,
//            @Path("securitySymbol") String securitySymbol
            @Path("securityId") long securityId
    );
    //</editor-fold>
}
