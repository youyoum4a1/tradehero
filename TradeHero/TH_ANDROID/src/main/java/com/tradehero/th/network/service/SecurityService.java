package com.tradehero.th.network.service;

import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.TransactionFormDTO;
import java.util.List;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/** Created with IntelliJ IDEA. User: xavier Date: 9/4/13 Time: 5:50 PM To change this template use File | Settings | File Templates. */
public interface SecurityService
{
    @GET("/securities/trending/")
    void getTrendingSecurities(
            Callback<List<SecurityCompactDTO>> callback);

    @GET("/securities/trending/")
    List<SecurityCompactDTO> getTrendingSecurities()
            throws RetrofitError;

    @GET("/securities/trendingVol/")
    void getTrendingSecuritiesByVolume(
            @Query("exchange") String exchange,
            Callback<List<SecurityCompactDTO>> callback);

    @GET("/securities/trendingVol/")
    List<SecurityCompactDTO> getTrendingSecuritiesByVolume(
            @Query("exchange") String exchange)
            throws RetrofitError;

    @GET("/securities/trendingPrice/")
    void getTrendingSecuritiesByPrice(
            @Query("exchange") String exchange,
            Callback<List<SecurityCompactDTO>> callback);

    @GET("/securities/trendingPrice/")
    List<SecurityCompactDTO> getTrendingSecuritiesByPrice(
            @Query("exchange") String exchange)
            throws RetrofitError;

    @GET("/securities/search")
    void searchSecurities(
            @Query("q") String searchString,
            @Query("page") int page,
            @Query("perPage") int perPage,
            Callback<List<SecurityCompactDTO>> callback);

    @GET("/securities/search")
    List<SecurityCompactDTO> searchSecurities(
            @Query("q") String searchString,
            @Query("page") int page,
            @Query("perPage") int perPage)
            throws RetrofitError;

    @GET("/securities/{exchange}/{securitySymbol}")
    void getSecurity(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            Callback<SecurityPositionDetailDTO> callback);

    @GET("/securities/{exchange}/{securitySymbol}")
    SecurityPositionDetailDTO getSecurity(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol)
            throws RetrofitError;

    @POST("/securities/{exchange}/{securitySymbol}/newbuy")
    void buy(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            @Query("tradeDto") TransactionFormDTO transactionFormDTO,
            Callback<SecurityPositionDetailDTO> callback);

    @POST("/securities/{exchange}/{securitySymbol}/newbuy")
    SecurityPositionDetailDTO buy(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            @Query("tradeDto") TransactionFormDTO transactionFormDTO);

    @POST("/securities/{exchange}/{securitySymbol}/newsell")
    void sell(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            @Query("tradeDto") TransactionFormDTO transactionFormDTO,
            Callback<SecurityPositionDetailDTO> callback);

    @POST("/securities/{exchange}/{securitySymbol}/newsell")
    SecurityPositionDetailDTO sell(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            @Query("tradeDto") TransactionFormDTO transactionFormDTO);
}
