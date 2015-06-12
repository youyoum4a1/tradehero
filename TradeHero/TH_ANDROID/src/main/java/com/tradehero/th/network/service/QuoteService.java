package com.tradehero.th.network.service;

import com.tradehero.chinabuild.data.KLineItem;
import com.tradehero.chinabuild.data.QuoteDetail;
import com.tradehero.chinabuild.data.QuoteTick;
import com.tradehero.chinabuild.data.SharePosition;
import com.tradehero.chinabuild.data.SignedQuote;
import com.tradehero.chinabuild.data.TradeRecord;
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
    @GET("/cn/v2/quotes/{securitySymbol}/detail")
    QuoteDetail getQuoteDetails(@Path("securitySymbol") String securitySymbol);

    @GET("/cn/v2/quotes/{securitySymbol}/detail")
    void getQuoteDetails(@Path("securitySymbol") String securitySymbol, Callback<QuoteDetail> callback);

    /**
     * Get quote
     *
     * @param securitySymbol
     * @param callback
     */
    @GET("/cn/v2/quotes/{securitySymbol}")
    void getQuote(@Path("securitySymbol") String securitySymbol, Callback<SignedQuote> callback);

    /**
     * Get quote ticks
     *
     * @param securitySymbol
     * @return
     */
    @GET("/cn/v2/quotes/{securitySymbol}/ticks")
    List<QuoteTick> getQuoteTicks(@Path("securitySymbol") String securitySymbol);

    @GET("/cn/v2/quotes/{securitySymbol}/ticks")
    void getQuoteTicks(@Path("securitySymbol") String securitySymbol, Callback<List<QuoteTick>> callback);

    /**
     * Get data for K-line
     * @param securitySymbol
     * @param type
     * @param callback
     */
    @GET("/cn/v2/quotes/{securitySymbol}/klines/{type}")
    void getKLines(@Path("securitySymbol") String securitySymbol, @Path("type") String type,
                   Callback<List<KLineItem>> callback);

    @GET("/securities/{exchange}/{securitySymbol}/quote")
    void getQuoteLegacy(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            Callback<SignedQuote> callback);

    @GET("/securities/compact")
    void getSecurityCompactDTO(@Query("exch") String exchange,
                               @Query("symbol") String symbol,
                               Callback<SecurityCompactDTO> callback);

    @GET("/cn/v2/securities/{exchange}/{securitySymbol}/trades")
    void getTradeRecords(@Path("exchange") String exchange,
                               @Path("securitySymbol") String securitySymbol,
                               @Query("page") Integer page,
                               @Query("perPage") Integer perPage,
                               Callback<List<TradeRecord>> callback);

    @GET("/cn/v2/securities/{exchange}/{securitySymbol}/positions")
    void getSharePositions(@Path("securitySymbol") String securitySymbol,
                         @Query("page") Integer page,
                         @Query("perPage") Integer perPage,
                         Callback<List<SharePosition>> callback);
}
