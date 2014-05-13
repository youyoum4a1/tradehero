package com.tradehero.th.network.service;

import com.tradehero.th.api.SignatureContainer;
import com.tradehero.th.api.quote.QuoteDTO;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;

interface QuoteServiceAsync
{
    //<editor-fold desc="Get Quote">
    @GET("/securities/{exchange}/{securitySymbol}/quote")
    void getQuote(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            Callback<SignatureContainer<QuoteDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="Get Raw Quote">
    @GET("/securities/{exchange}/{securitySymbol}/quote")
    void getRawQuote(
                @Path("exchange") String exchange,
                @Path("securitySymbol") String securitySymbol,
                Callback<Response> callback);
    //</editor-fold>
}
