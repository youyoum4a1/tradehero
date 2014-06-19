package com.tradehero.th.network.service;

import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.TransactionFormDTO;
import retrofit.http.*;

import java.util.List;
import java.util.Map;

public interface SecurityService
{
    //<editor-fold desc="Get Multiple Securities">
    @GET("/securities/multi/")
    Map<Integer, SecurityCompactDTO> getMultipleSecurities(
            @Query("securityIds") String commaSeparatedIntegerIds);
    //</editor-fold>

    //<editor-fold desc="Get Basic Trending">
    @GET("/securities/trending/")
    List<SecurityCompactDTO> getTrendingSecurities(
            @Query("exchange") String exchange,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Trending By Volume">
    @GET("/securities/trendingVol/")
    List<SecurityCompactDTO> getTrendingSecuritiesByVolume(
            @Query("exchange") String exchange,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Trending By Price">
    @GET("/securities/trendingPrice/")
    List<SecurityCompactDTO> getTrendingSecuritiesByPrice(
            @Query("exchange") String exchange,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Trending For All">
    @GET("/securities/trendingExchange/")
    List<SecurityCompactDTO> getTrendingSecuritiesAllInExchange(
            @Query("exchange") String exchange,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Search Securities">
    @GET("/securities/search")
    List<SecurityCompactDTO> searchSecurities(
            @Query("q") String searchString,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Security">
    @GET("/securities/{exchange}/{pathSafeSecuritySymbol}")
    SecurityPositionDetailDTO getSecurity(
            @Path("exchange") String exchange,
            @Path("pathSafeSecuritySymbol") String pathSafeSecuritySymbol);
    //</editor-fold>

    //<editor-fold desc="Buy Security">
    @POST("/securities/{exchange}/{securitySymbol}/newbuy")
    SecurityPositionDetailDTO buy(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            @Body() TransactionFormDTO transactionFormDTO);
    //</editor-fold>

    //<editor-fold desc="Sell Security">
    @POST("/securities/{exchange}/{securitySymbol}/newsell")
    SecurityPositionDetailDTO sell(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            @Body() TransactionFormDTO transactionFormDTO);
    //</editor-fold>
}
