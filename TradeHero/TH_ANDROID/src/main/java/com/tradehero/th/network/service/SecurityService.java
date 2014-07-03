package com.tradehero.th.network.service;

import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.TransactionFormDTO;
import java.util.Map;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface SecurityService
{
    //<editor-fold desc="Get Multiple Securities">
    @GET("/securities/multi/")
    Map<Integer, SecurityCompactDTO> getMultipleSecurities(
            @Query("securityIds") String commaSeparatedIntegerIds);
    //</editor-fold>

    //<editor-fold desc="Get Basic Trending">
    @GET("/securities/trending/")
    SecurityCompactDTOList getTrendingSecurities(
            @Query("exchange") String exchange,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Trending By Volume">
    @GET("/securities/trendingVol/")
    SecurityCompactDTOList getTrendingSecuritiesByVolume(
            @Query("exchange") String exchange,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Trending By Price">
    @GET("/securities/trendingPrice/")
    SecurityCompactDTOList getTrendingSecuritiesByPrice(
            @Query("exchange") String exchange,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Trending For All">
    @GET("/securities/trendingExchange/")
    SecurityCompactDTOList getTrendingSecuritiesAllInExchange(
            @Query("exchange") String exchange,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Search Securities">
    @GET("/securities/search")
    SecurityCompactDTOList searchSecurities(
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
