package com.tradehero.th.network.service;

import com.tradehero.th.api.competition.HelpVideoDTOList;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface ProviderService
{
    //<editor-fold desc="Get Providers">
    @GET("/providers") ProviderDTOList getProviders();
    //</editor-fold>

    //<editor-fold desc="Get Provider Securities">
    @GET("/providers/{providerId}/securities")
    SecurityCompactDTOList getSecurities(
            @Path("providerId") int providerId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Provider Warrant Underlyers">
    @GET("/providers/{providerId}/warrantUnderlyers")
    SecurityCompactDTOList getWarrantUnderlyers(
            @Path("providerId") int providerId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Search Provider Securities">
    @GET("/providers/{providerId}/securities")
    SecurityCompactDTOList searchSecurities(
            @Path("providerId") int providerId,
            @Query("q") String searchString,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Help Videos">
    @GET("/providers/{providerId}/helpVideos")
    HelpVideoDTOList getHelpVideos(
            @Path("providerId") int providerId);
    //</editor-fold>
}
