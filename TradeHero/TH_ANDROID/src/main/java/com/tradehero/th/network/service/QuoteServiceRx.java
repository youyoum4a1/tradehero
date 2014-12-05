package com.tradehero.th.network.service;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.SignatureContainer;
import com.tradehero.th.api.quote.QuoteDTO;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

interface QuoteServiceRx
{
    //<editor-fold desc="Get Quote">
    @GET("/securities/{exchange}/{securitySymbol}/quote")
    Observable<SignatureContainer<QuoteDTO>> getQuote(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol);
    //</editor-fold>

    //<editor-fold desc="Get Raw Quote">
    // We should not ask Observable<Response> from Retrofit as Response is not a json convertible object
    @GET("/securities/{exchange}/{securitySymbol}/quote")
    void getRawQuote(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            Callback<BaseResponseDTO> callback);
    //</editor-fold>
}
