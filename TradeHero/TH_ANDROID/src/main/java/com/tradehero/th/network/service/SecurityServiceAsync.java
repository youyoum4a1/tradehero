package com.tradehero.th.network.service;

import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.TransactionFormDTO;
import retrofit.Callback;
import retrofit.http.*;

import java.util.List;
import java.util.Map;

interface SecurityServiceAsync
{
    //<editor-fold desc="Get Multiple Securities">
    @GET("/securities/multi/")
    void getMultipleSecurities(
            @Query("securityIds") String ids,
            Callback<Map<Integer, SecurityCompactDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="Get Basic Trending">
    @GET("/securities/trending/")
    void getTrendingSecurities(
            @Query("exchange") String exchange,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            Callback<List<SecurityCompactDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="Get Trending By Volume">
    @GET("/securities/trendingVol/")
    void getTrendingSecuritiesByVolume(
            @Query("exchange") String exchange,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            Callback<List<SecurityCompactDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="Get Trending By Price">
    @GET("/securities/trendingPrice/")
    void getTrendingSecuritiesByPrice(
            @Query("exchange") String exchange,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            Callback<List<SecurityCompactDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="Get Trending For All">
    @GET("/securities/trendingExchange/")
    void getTrendingSecuritiesAllInExchange(
            @Query("exchange") String exchange,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            Callback<List<SecurityCompactDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="Search Securities">
    @GET("/securities/search")
    void searchSecurities(
            @Query("q") String searchString,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            Callback<List<SecurityCompactDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="Get Security">
    @GET("/securities/{exchange}/{securitySymbol}")
    void getSecurity(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            Callback<SecurityPositionDetailDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Buy Security">
    @POST("/securities/{exchange}/{securitySymbol}/newbuy")
    void buy(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            @Body() TransactionFormDTO transactionFormDTO,
            Callback<SecurityPositionDetailDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Sell Security">
    @POST("/securities/{exchange}/{securitySymbol}/newsell")
    void sell(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            @Body() TransactionFormDTO transactionFormDTO,
            Callback<SecurityPositionDetailDTO> callback);
    //</editor-fold>
}
