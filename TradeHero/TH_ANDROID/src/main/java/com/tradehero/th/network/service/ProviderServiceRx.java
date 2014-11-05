package com.tradehero.th.network.service;

import com.tradehero.th.api.competition.HelpVideoDTOList;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.api.competition.ProviderDisplayCellDTOList;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface ProviderServiceRx
{
    //<editor-fold desc="Get Providers">
    @GET("/providers")
    Observable<ProviderDTOList> getProviders();
    //</editor-fold>

    //<editor-fold desc="Get Provider">
    @GET("/providers/{providerId}")
    Observable<ProviderDTO> getProvider(@Path("providerId") int providerId);
    //</editor-fold>

    //<editor-fold desc="Get Provider Portfolio">
    @GET("/providers/{providerId}/portfolio")
    Observable<PortfolioDTO> getPortfolio(
            @Path("providerID") int providerId);
    //</editor-fold>

    //<editor-fold desc="Get Provider Securities">
    @GET("/providers/{providerId}/securities")
    Observable<SecurityCompactDTOList> getSecurities(
            @Path("providerId") int providerId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Provider Warrant Underlyers">
    @GET("/providers/{providerId}/warrantUnderlyers")
    Observable<SecurityCompactDTOList> getWarrantUnderlyers(
            @Path("providerId") int providerId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Search Provider Securities">
    @GET("/providers/{providerId}/securities")
    Observable<SecurityCompactDTOList> searchSecurities(
            @Path("providerId") int providerId,
            @Query("q") String searchString,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Help Videos">
    @GET("/providers/{providerId}/helpVideos")
    Observable<HelpVideoDTOList> getHelpVideos(
            @Path("providerId") int providerId);
    //</editor-fold>

    //<editor-fold desc="Get Cells">
    @GET("/providers/{providerId}/displaycells")
    Observable<ProviderDisplayCellDTOList> getDisplayCells(
            @Path("providerId") int providerId);
    //</editor-fold>
}
