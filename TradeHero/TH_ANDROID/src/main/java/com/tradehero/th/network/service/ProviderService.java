package com.tradehero.th.network.service;

import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import java.util.List;
import java.util.Map;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/** Created with IntelliJ IDEA. User: xavier Date: 10/10/13 Time: 4:51 PM To change this template use File | Settings | File Templates. */
public interface ProviderService
{
    //<editor-fold desc="Get Providers">
    @GET("/providers")
    List<ProviderDTO> getProviders()
            throws RetrofitError;

    @GET("/providers")
    void getProviders(Callback<List<ProviderDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="Get Provider Securities">
    @GET("/providers/{providerId}/securities")
    List<SecurityCompactDTO> getSecurities(
            @Path("providerId") int providerId)
            throws RetrofitError;

    @GET("/providers/{providerId}/securities")
    void getSecurities(
            @Path("providerId") int providerId,
            Callback<List<SecurityCompactDTO>> callback);

    @GET("/providers/{providerId}/securities")
    List<SecurityCompactDTO> getSecurities(
            @Path("providerId") int providerId,
            @Query("page") int page)
            throws RetrofitError;

    @GET("/providers/{providerId}/securities")
    void getSecurities(
            @Path("providerId") int providerId,
            @Query("page") int page,
            Callback<List<SecurityCompactDTO>> callback);

    @GET("/providers/{providerId}/securities")
    List<SecurityCompactDTO> getSecurities(
            @Path("providerId") int providerId,
            @Query("page") int page,
            @Query("perPage") int perPage)
            throws RetrofitError;

    @GET("/providers/{providerId}/securities")
    void getSecurities(
            @Path("providerId") int providerId,
            @Query("page") int page,
            @Query("perPage") int perPage,
            Callback<List<SecurityCompactDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="Get Provider Warrant Underlyers">
    @GET("/providers/{providerId}/warrantUnderlyers")
    List<SecurityCompactDTO> getWarrantUnderlyers(
            @Path("providerId") int providerId)
            throws RetrofitError;

    @GET("/providers/{providerId}/warrantUnderlyers")
    void getWarrantUnderlyers(
            @Path("providerId") int providerId,
            Callback<List<SecurityCompactDTO>> callback);

    @GET("/providers/{providerId}/warrantUnderlyers")
    List<SecurityCompactDTO> getWarrantUnderlyers(
            @Path("providerId") int providerId,
            @Query("page") int page)
            throws RetrofitError;

    @GET("/providers/{providerId}/warrantUnderlyers")
    void getWarrantUnderlyers(
            @Path("providerId") int providerId,
            @Query("page") int page,
            Callback<List<SecurityCompactDTO>> callback);

    @GET("/providers/{providerId}/warrantUnderlyers")
    List<SecurityCompactDTO> getWarrantUnderlyers(
            @Path("providerId") int providerId,
            @Query("page") int page,
            @Query("perPage") int perPage)
            throws RetrofitError;

    @GET("/providers/{providerId}/warrantUnderlyers")
    void getWarrantUnderlyers(
            @Path("providerId") int providerId,
            @Query("page") int page,
            @Query("perPage") int perPage,
            Callback<List<SecurityCompactDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="Get Provider Suggested Warrant">
    @GET("/providers/{providerId}/suggestedWarrant")
    Map<String, Object> getSuggestedWarrant( // TODO find out what Object we get
            @Path("providerId") int providerId,
            @Query("underlyingSecurityId") int underlyingSecurityId,
            @Query("price") double price,
            @Query("date") String date)
            throws RetrofitError;

    @GET("/providers/{providerId}/suggestedWarrant")
    void getSuggestedWarrant(
            @Path("providerId") int providerId,
            @Query("underlyingSecurityId") int underlyingSecurityId,
            @Query("price") double price,
            @Query("date") String date,
            Callback<Map<String, Object>> callback);

    @GET("/providers/{providerId}/suggestedWarrant")
    Map<String, Object> getSuggestedWarrant(
            @Path("providerId") int providerId,
            @Query("underlyingSecurityId") int underlyingSecurityId,
            @Query("price") double price,
            @Query("date") String date,
            @Query("page") int page)
            throws RetrofitError;

    @GET("/providers/{providerId}/suggestedWarrant")
    void getSuggestedWarrant(
            @Path("providerId") int providerId,
            @Query("underlyingSecurityId") int underlyingSecurityId,
            @Query("price") double price,
            @Query("date") String date,
            @Query("page") int page,
            Callback<Map<String, Object>> callback);

    @GET("/providers/{providerId}/suggestedWarrant")
    Map<String, Object> getSuggestedWarrant(
            @Path("providerId") int providerId,
            @Query("underlyingSecurityId") int underlyingSecurityId,
            @Query("price") double price,
            @Query("date") String date,
            @Query("page") int page,
            @Query("perPage") int perPage)
            throws RetrofitError;

    @GET("/providers/{providerId}/suggestedWarrant")
    void getSuggestedWarrant(
            @Path("providerId") int providerId,
            @Query("underlyingSecurityId") int underlyingSecurityId,
            @Query("price") double price,
            @Query("date") String date,
            @Query("page") int page,
            @Query("perPage") int perPage,
            Callback<Map<String, Object>> callback);
    //</editor-fold>
}
