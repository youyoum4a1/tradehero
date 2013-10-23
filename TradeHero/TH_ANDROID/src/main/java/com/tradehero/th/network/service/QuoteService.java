package com.tradehero.th.network.service;

import com.tradehero.th.api.SignatureContainer;
import com.tradehero.th.api.quote.QuoteDTO;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;

/** Created with IntelliJ IDEA. User: xavier Date: 10/7/13 Time: 4:28 PM To change this template use File | Settings | File Templates. */
public interface QuoteService
{
    //<editor-fold desc="Get Quote">
    @GET("/securities/{exchange}/{securitySymbol}/quote")
    void getQuote(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            Callback<SignatureContainer<QuoteDTO>> callback);

    @GET("/securities/{exchange}/{securitySymbol}/quote")
    SignatureContainer<QuoteDTO> getQuote(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol)
        throws RetrofitError;
    //</editor-fold>
}
