package com.tradehero.th.network.service;

import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.TransactionFormDTO;
import java.util.List;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;


public interface SecurityService
{
    //get multiple securities
    @GET("/securities/multi/")
    void getMultipleSecurities(
            @Query("securityIds") String ids,
            Callback<List<SecurityCompactDTO>> callback);

    @GET("/securities/multi/")
    void getMultipleSecurities2(
            @Query("securityIds") String ids,
            Callback<Response> callback);

    //get multiple securities
    @GET("/securities/multi/")
    List<SecurityCompactDTO> getMultipleSecurities(
            @Query("securityIds") String ids);

    //<editor-fold desc="Get Basic Trending">
    @GET("/securities/trending/")
    void getTrendingSecurities(
            Callback<List<SecurityCompactDTO>> callback);

    @GET("/securities/trending/")
    List<SecurityCompactDTO> getTrendingSecurities()
        throws RetrofitError;

    @GET("/securities/trending/")
    void getTrendingSecurities(
            @Query("exchange") String exchange,
            Callback<List<SecurityCompactDTO>> callback);

    @GET("/securities/trending/")
    List<SecurityCompactDTO> getTrendingSecurities(
            @Query("exchange") String exchange)
        throws RetrofitError;

    @GET("/securities/trending/")
    void getTrendingSecurities(
            @Query("exchange") String exchange,
            @Query("page") int page,
            Callback<List<SecurityCompactDTO>> callback);

    @GET("/securities/trending/")
    List<SecurityCompactDTO> getTrendingSecurities(
            @Query("exchange") String exchange,
            @Query("page") int page)
        throws RetrofitError;

    @GET("/securities/trending/")
    void getTrendingSecurities(
            @Query("exchange") String exchange,
            @Query("page") int page,
            @Query("perPage") int perPage,
            Callback<List<SecurityCompactDTO>> callback);

    @GET("/securities/trending/")
    List<SecurityCompactDTO> getTrendingSecurities(
            @Query("exchange") String exchange,
            @Query("page") int page,
            @Query("perPage") int perPage)
        throws RetrofitError;
    //</editor-fold>

    //<editor-fold desc="Get Trending By Volume">
    @GET("/securities/trendingVol/")
    void getTrendingSecuritiesByVolume(
            Callback<List<SecurityCompactDTO>> callback);

    @GET("/securities/trendingVol/")
    List<SecurityCompactDTO> getTrendingSecuritiesByVolume()
        throws RetrofitError;

    @GET("/securities/trendingVol/")
    void getTrendingSecuritiesByVolume(
            @Query("exchange") String exchange,
            Callback<List<SecurityCompactDTO>> callback);

    @GET("/securities/trendingVol/")
    List<SecurityCompactDTO> getTrendingSecuritiesByVolume(
            @Query("exchange") String exchange)
        throws RetrofitError;

    @GET("/securities/trendingVol/")
    void getTrendingSecuritiesByVolume(
            @Query("exchange") String exchange,
            @Query("page") int page,
            Callback<List<SecurityCompactDTO>> callback);

    @GET("/securities/trendingVol/")
    List<SecurityCompactDTO> getTrendingSecuritiesByVolume(
            @Query("exchange") String exchange,
            @Query("page") int page)
        throws RetrofitError;

    @GET("/securities/trendingVol/")
    void getTrendingSecuritiesByVolume(
            @Query("exchange") String exchange,
            @Query("page") int page,
            @Query("perPage") int perPage,
            Callback<List<SecurityCompactDTO>> callback);

    @GET("/securities/trendingVol/")
    List<SecurityCompactDTO> getTrendingSecuritiesByVolume(
            @Query("exchange") String exchange,
            @Query("page") int page,
            @Query("perPage") int perPage)
        throws RetrofitError;
    //</editor-fold>

    //<editor-fold desc="Get Trending By Price">
    @GET("/securities/trendingPrice/")
    void getTrendingSecuritiesByPrice(
            Callback<List<SecurityCompactDTO>> callback);

    @GET("/securities/trendingPrice/")
    List<SecurityCompactDTO> getTrendingSecuritiesByPrice()
        throws RetrofitError;

    @GET("/securities/trendingPrice/")
    void getTrendingSecuritiesByPrice(
            @Query("exchange") String exchange,
            Callback<List<SecurityCompactDTO>> callback);

    @GET("/securities/trendingPrice/")
    List<SecurityCompactDTO> getTrendingSecuritiesByPrice(
            @Query("exchange") String exchange)
        throws RetrofitError;

    @GET("/securities/trendingPrice/")
    void getTrendingSecuritiesByPrice(
            @Query("exchange") String exchange,
            @Query("page") int page,
            Callback<List<SecurityCompactDTO>> callback);

    @GET("/securities/trendingPrice/")
    List<SecurityCompactDTO> getTrendingSecuritiesByPrice(
            @Query("exchange") String exchange,
            @Query("page") int page)
        throws RetrofitError;

    @GET("/securities/trendingPrice/")
    void getTrendingSecuritiesByPrice(
            @Query("exchange") String exchange,
            @Query("page") int page,
            @Query("perPage") int perPage,
            Callback<List<SecurityCompactDTO>> callback);

    @GET("/securities/trendingPrice/")
    List<SecurityCompactDTO> getTrendingSecuritiesByPrice(
            @Query("exchange") String exchange,
            @Query("page") int page,
            @Query("perPage") int perPage)
        throws RetrofitError;
    //</editor-fold>

    //<editor-fold desc="Get Trending For All">
    @GET("/securities/trendingExchange/")
    void getTrendingSecuritiesAllInExchange(
            Callback<List<SecurityCompactDTO>> callback);

    @GET("/securities/trendingExchange/")
    List<SecurityCompactDTO> getTrendingSecuritiesAllInExchange()
        throws RetrofitError;

    @GET("/securities/trendingExchange/")
    void getTrendingSecuritiesAllInExchange(
            @Query("exchange") String exchange,
            Callback<List<SecurityCompactDTO>> callback);

    @GET("/securities/trendingExchange/")
    List<SecurityCompactDTO> getTrendingSecuritiesAllInExchange(
            @Query("exchange") String exchange)
        throws RetrofitError;

    @GET("/securities/trendingExchange/")
    void getTrendingSecuritiesAllInExchange(
            @Query("exchange") String exchange,
            @Query("page") int page,
            Callback<List<SecurityCompactDTO>> callback);

    @GET("/securities/trendingExchange/")
    List<SecurityCompactDTO> getTrendingSecuritiesAllInExchange(
            @Query("exchange") String exchange,
            @Query("page") int page)
        throws RetrofitError;

    @GET("/securities/trendingExchange/")
    void getTrendingSecuritiesAllInExchange(
            @Query("exchange") String exchange,
            @Query("page") int page,
            @Query("perPage") int perPage,
            Callback<List<SecurityCompactDTO>> callback);

    @GET("/securities/trendingExchange/")
    List<SecurityCompactDTO> getTrendingSecuritiesAllInExchange(
            @Query("exchange") String exchange,
            @Query("page") int page,
            @Query("perPage") int perPage)
        throws RetrofitError;
    //</editor-fold>

    //<editor-fold desc="Search Securities">
    @GET("/securities/search")
    void searchSecurities(
            @Query("q") String searchString,
            Callback<List<SecurityCompactDTO>> callback);

    @GET("/securities/search")
    List<SecurityCompactDTO> searchSecurities(
            @Query("q") String searchString)
        throws RetrofitError;

    @GET("/securities/search")
    void searchSecurities(
            @Query("q") String searchString,
            @Query("page") int page,
            Callback<List<SecurityCompactDTO>> callback);

    @GET("/securities/search")
    List<SecurityCompactDTO> searchSecurities(
            @Query("q") String searchString,
            @Query("page") int page)
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
    //</editor-fold>

    //<editor-fold desc="Get Security">
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
    //</editor-fold>

    //<editor-fold desc="Buy Security">
    @POST("/securities/{exchange}/{securitySymbol}/newbuy")
    void buy(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            @Body() TransactionFormDTO transactionFormDTO,
            Callback<SecurityPositionDetailDTO> callback);

    @POST("/securities/{exchange}/{securitySymbol}/newbuy")
    SecurityPositionDetailDTO buy(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            @Body() TransactionFormDTO transactionFormDTO)
        throws RetrofitError;
    //</editor-fold>

    //<editor-fold desc="Sell Security">
    @POST("/securities/{exchange}/{securitySymbol}/newsell")
    void sell(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            @Body() TransactionFormDTO transactionFormDTO,
            Callback<SecurityPositionDetailDTO> callback);

    @POST("/securities/{exchange}/{securitySymbol}/newsell")
    SecurityPositionDetailDTO sell(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            @Body() TransactionFormDTO transactionFormDTO)
        throws RetrofitError;
    //</editor-fold>
}
