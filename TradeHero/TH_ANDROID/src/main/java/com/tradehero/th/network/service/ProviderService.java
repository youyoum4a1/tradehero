package com.tradehero.th.network.service;

import com.tradehero.th.api.competition.HelpVideoDTO;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import java.util.List;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface ProviderService
{
    //<editor-fold desc="Get Providers">
    @GET("/providers")
    List<ProviderDTO> getProviders();
    //</editor-fold>

    //<editor-fold desc="Get Provider Securities">
    @GET("/providers/{providerId}/securities")
    List<SecurityCompactDTO> getSecurities(
            @Path("providerId") int providerId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Provider Warrant Underlyers">
    @GET("/providers/{providerId}/warrantUnderlyers")
    List<SecurityCompactDTO> getWarrantUnderlyers(
            @Path("providerId") int providerId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Search Provider Securities">
    @GET("/providers/{providerId}/securities")
    List<SecurityCompactDTO> searchSecurities(
            @Path("providerId") int providerId,
            @Query("q") String searchString,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Help Videos">
    @GET("/providers/{providerId}/helpVideos")
    List<HelpVideoDTO> getHelpVideos(
            @Path("providerId") int providerId);
    //</editor-fold>
}
