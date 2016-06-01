package com.ayondo.academy.network.service;

import com.ayondo.academy.api.fx.FXChartDTO;
import com.ayondo.academy.api.portfolio.OwnedPortfolioIdList;
import com.ayondo.academy.api.position.PositionDTOList;
import com.ayondo.academy.api.position.SecurityPositionTransactionDTO;
import com.ayondo.academy.api.quote.QuoteDTO;
import com.ayondo.academy.api.security.SecurityCompactDTO;
import com.ayondo.academy.api.security.SecurityCompactDTOList;
import com.ayondo.academy.api.security.TransactionFormDTO;
import java.util.List;
import java.util.Map;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface SecurityServiceRx
{
    //<editor-fold desc="Get Multiple Securities">
    @GET("/securities/multi/")
    Observable<Map<Integer, SecurityCompactDTO>> getMultipleSecurities(
            @Query("securityIds") String commaSeparatedIntegerIds);
    //</editor-fold>

    //<editor-fold desc="Get Basic Trending">
    @GET("/securities/trending/")
    Observable<SecurityCompactDTOList> getTrendingSecurities(
            @Query("exchange") String exchange,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Trending By Volume">
    @GET("/securities/trendingVol/")
    Observable<SecurityCompactDTOList> getTrendingSecuritiesByVolume(
            @Query("exchange") String exchange,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Trending By Price">
    @GET("/securities/trendingPrice/")
    Observable<SecurityCompactDTOList> getTrendingSecuritiesByPrice(
            @Query("exchange") String exchange,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Trending For All">
    @GET("/securities/trendingExchange/")
    Observable<SecurityCompactDTOList> getTrendingSecuritiesAllInExchange(
            @Query("exchange") String exchange,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Search Securities">
    @GET("/securities/search")
    Observable<SecurityCompactDTOList> searchSecurities(
            @Query("q") String searchString,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get List By Sector and Exchange">
    @GET("/securities/bySectorAndExchange")
    Observable<SecurityCompactDTOList> getBySectorAndExchange(
            @Query("exchange") Integer exchangeId,
            @Query("sector") Integer sectorId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/securities/bySectorsAndExchanges")
    Observable<SecurityCompactDTOList> getBySectorsAndExchanges(
            @Query("exchanges") String commaSeparatedExchangeIds,
            @Query("sectors") String commaSeparatedSectorIds,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Security">
    @GET("/securities/applicablePortfolios")
    Observable<OwnedPortfolioIdList> getApplicablePortfolioIds(
            @Query("exch") String exchange,
            @Query("symbol") String securitySymbol);

    @GET("/securities/positions")
    Observable<PositionDTOList> getPositions(
            @Query("exch") String exchange,
            @Query("symbol") String securitySymbol);

    @GET("/securities/compact")
    Observable<SecurityCompactDTO> getCompactSecurity(
            @Query("exch") String exchange,
            @Query("symbol") String securitySymbol);
    //</editor-fold>

    //<editor-fold desc="Buy Security">
    @POST("/securities/{exchange}/{securitySymbol}/newbuy")
    Observable<SecurityPositionTransactionDTO> buy(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            @Body() TransactionFormDTO transactionFormDTO);

    @POST("/securities/{exchange}/{securitySymbol}/fxbuy")
    Observable<SecurityPositionTransactionDTO> buyFx(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            @Body() TransactionFormDTO transactionFormDTO);
    //</editor-fold>

    //<editor-fold desc="Sell Security">
    @POST("/securities/{exchange}/{securitySymbol}/newsell")
    Observable<SecurityPositionTransactionDTO> sell(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            @Body() TransactionFormDTO transactionFormDTO);

    @POST("/securities/{exchange}/{securitySymbol}/fxsell")
    Observable<SecurityPositionTransactionDTO> sellFx(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            @Body() TransactionFormDTO transactionFormDTO);
    //</editor-fold>

    //<editor-fold desc="Get Basic FX Trending">
    @GET("/securities/trendingFx")
    Observable<SecurityCompactDTOList> getFXSecurities();
    //</editor-fold>

    //<editor-fold desc="Get FX KChart">
    @GET("/FX/{securitySymbol}/{granularity}/history")
    Observable<FXChartDTO> getFXHistory(
            @Path("securitySymbol") String securitySymbol,
            @Path("granularity") String granularity);
    //</editor-fold>

    //<editor-fold desc="Get FX All Price">
    @GET("/FX/batchFxQuote")
    Observable<List<QuoteDTO>> getFXSecuritiesAllPrice();
    //</editor-fold>
}

