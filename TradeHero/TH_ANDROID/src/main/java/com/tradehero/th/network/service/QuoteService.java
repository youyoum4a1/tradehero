package com.tradehero.th.network.service;

import com.tradehero.chinabuild.data.KLineItem;
import com.tradehero.chinabuild.data.QuoteDetail;
import com.tradehero.chinabuild.data.QuoteTick;
import com.tradehero.chinabuild.data.SecurityUserPositionDTO;
import com.tradehero.chinabuild.data.SignedQuote;
import com.tradehero.chinabuild.data.SecurityUserOptDTO;
import com.tradehero.chinabuild.fragment.security.SecurityOptPositionsList;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

interface QuoteService
{

    //<editor-fold desc="Get Raw Quote">
    @GET("/securities/{exchange}/{securitySymbol}/quote")
    void getRawQuote(
                @Path("exchange") String exchange,
                @Path("securitySymbol") String securitySymbol,
                Callback<Response> callback);
    //</editor-fold>

    /**
     * Get quote details
     *
     * @param securitySymbol
     * @return
     */
    @GET("/cn/v2/quotes/{exchange}/{securitySymbol}/detail")
    void getQuoteDetails(@Path("exchange") String exchange,
                         @Path("securitySymbol") String securitySymbol,
                         Callback<QuoteDetail> callback);

    /**
     * Get quote
     *
     * @param securitySymbol
     * @param callback
     */
    @GET("/cn/v2/quotes/{exchange}/{securitySymbol}")
    void getQuote(@Path("exchange") String exchange,
                  @Path("securitySymbol") String securitySymbol,
                  Callback<Response> callback);

    /**
     * Get quote ticks
     *
     * @param securitySymbol
     * @return
     */
    @GET("/cn/v2/quotes/{exchange}/{securitySymbol}/ticks")
    void getQuoteTicks(@Path("exchange") String exchange,
                       @Path("securitySymbol") String securitySymbol,
                       Callback<List<QuoteTick>> callback);

    /**
     * Get data for K-line
     * @param securitySymbol
     * @param type
     * @param callback
     */
    @GET("/cn/v2/quotes/{exchange}/{securitySymbol}/klines/{type}")
    void getKLines(@Path("exchange") String exchange,
                   @Path("securitySymbol") String securitySymbol,
                   @Path("type") String type,
                   Callback<List<KLineItem>> callback);

    @GET("/securities/{exchange}/{securitySymbol}/quote")
    void getQuoteLegacy(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            Callback<Response> callback);

    @GET("/securities/{exchange}/{securitySymbol}/quote")
    void getQuoteForSecurityOpt(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            Callback<Response> callback);

    @GET("/securities/compact")
    void getSecurityCompactDTO(@Query("exch") String exchange,
                               @Query("symbol") String symbol,
                               Callback<SecurityCompactDTO> callback);

    @GET("/cn/v2/securities/{exchange}/{securitySymbol}/trades")
    void getTradeRecords(@Path("exchange") String exchange,
                               @Path("securitySymbol") String securitySymbol,
                               @Query("page") Integer page,
                               @Query("perPage") Integer perPage,
                               Callback<List<SecurityUserOptDTO>> callback);

    @GET("/cn/v2/securities/{exchange}/{securitySymbol}/positions")
    void getSharePositions(@Path("exchange") String exchange,
                           @Path("securitySymbol") String securitySymbol,
                           @Query("page") Integer page,
                           @Query("perPage") Integer perPage,
                           Callback<List<SecurityUserPositionDTO>> callback);

    @GET("/cn/v2/positions/open")
    void retrieveMainPositions(Callback<SecurityOptPositionsList> callback);
}
