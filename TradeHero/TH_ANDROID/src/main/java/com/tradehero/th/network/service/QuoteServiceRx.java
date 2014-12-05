package com.tradehero.th.network.service;

import com.tradehero.th.api.SignatureContainer;
import com.tradehero.th.api.quote.QuoteDTO;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

interface QuoteServiceRx
{
    //<editor-fold desc="Get Raw Quote">
    // We should not ask Observable<Response> from Retrofit as Response is not a json convertible object
    @GET("/securities/{exchange}/{securitySymbol}/quote")
    void getRawQuote(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            Callback<QuoteSignatureContainer> callback);
    //</editor-fold>

    static class QuoteSignatureContainer extends SignatureContainer<QuoteDTO>{}
}
