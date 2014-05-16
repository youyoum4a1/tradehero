package com.tradehero.th.network.service;

import com.tradehero.th.api.SignatureContainer;
import com.tradehero.th.api.quote.QuoteDTO;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;

public interface QuoteService
{
    //<editor-fold desc="Get Quote">
    @GET("/securities/{exchange}/{securitySymbol}/quote")
    SignatureContainer<QuoteDTO> getQuote(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol);
    //</editor-fold>

    //<editor-fold desc="Get Raw Quote">
    @GET("/securities/{exchange}/{securitySymbol}/quote")
    Response getRawQuote(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol);
    //</editor-fold>
}
