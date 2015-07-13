package com.tradehero.th.network.service;

import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.SecurityCompactExtraDTOList;
import com.tradehero.th.api.security.TransactionFormDTO;

import retrofit.Callback;
import retrofit.http.*;

public interface SecurityService
{

    //<editor-fold desc="Get Basic Trending">
    @GET("/securities/trending/") SecurityCompactDTOList getTrendingSecurities(
            @Query("exchange") String exchange,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Trending By Volume">
    @GET("/securities/trendingVol/") SecurityCompactDTOList getTrendingSecuritiesByVolume(
            @Query("exchange") String exchange,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Trending By Price">
    @GET("/securities/trendingPrice/") SecurityCompactDTOList getTrendingSecuritiesByPrice(
            @Query("exchange") String exchange,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Trending For All">
    //@GET("/securities/trendingExchange/")
    //http://localhost/api/securities/trendingHold?exchange=SHA
    //http://localhost/api/securities/trendingWatch?exchange=SHA

    @GET("/securities/trendingRisePercent/") SecurityCompactExtraDTOList getTrendingSecuritiesAllInRisePercent(
            @Query("exchange") String exchange,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/securities/trendingWatch/") SecurityCompactExtraDTOList getTrendingSecuritiesAllInExchangeWatch(
            @Query("exchange") String exchange,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/securities/trendingHold/") SecurityCompactExtraDTOList getTrendingSecuritiesAllInExchangeHold(
            @Query("exchange") String exchange,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/securities/trendingMarketCap/") SecurityCompactExtraDTOList getTrendingSecuritiesAllInExchangeChinaConcept(
            @Query("exchange") String exchange,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    //搜索比赛股票列表
    @GET("/usercompetitions/{competitionId}/securities") SecurityCompactDTOList getTrendingSecuritiesAllInCompetition(
            @Path("competitionId") int competitionId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    //通过关键字搜索股票列表
    @GET("/usercompetitions/{competitionId}/securities") SecurityCompactDTOList getTrendingSecuritiesAllInCompetitionSearch(
            @Path("competitionId") int competitionId,
            @Query("q") String q,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    //</editor-fold>

    //<editor-fold desc="Search Securities">
    @GET("/securities/search") SecurityCompactDTOList searchSecurities(
            @Query("q") String searchString,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Search hot Securities">
    @GET("/securities/trendingSearch") SecurityCompactExtraDTOList searchHotSecurities(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Security">
    @GET("/securities/{exchange}/{pathSafeSecuritySymbol}?includeProviderInfo=false") SecurityPositionDetailDTO getSecurity(
            @Path("exchange") String exchange,
            @Path("pathSafeSecuritySymbol") String pathSafeSecuritySymbol);
    //</editor-fold>

    //<editor-fold desc="Buy Security">
    @POST("/securities/{exchange}/{securitySymbol}/newbuy") SecurityPositionDetailDTO buy(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            @Body() TransactionFormDTO transactionFormDTO);
    //</editor-fold>

    //<editor-fold desc="Sell Security">
    @POST("/securities/{exchange}/{securitySymbol}/newsell") SecurityPositionDetailDTO sell(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            @Body() TransactionFormDTO transactionFormDTO);
    //</editor-fold>


}
