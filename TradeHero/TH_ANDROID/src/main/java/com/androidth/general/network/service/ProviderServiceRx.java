package com.androidth.general.network.service;

import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.competition.CompetitionPreSeasonDTO;
import com.androidth.general.api.competition.CompetitionPreseasonShareFormDTO;
import com.androidth.general.api.competition.HelpVideoDTOList;
import com.androidth.general.api.competition.ProviderDTO;
import com.androidth.general.api.competition.ProviderDTOList;
import com.androidth.general.api.competition.ProviderDisplayCellDTOList;
import com.androidth.general.api.competition.ProviderPrizePoolDTO;
import com.androidth.general.api.portfolio.PortfolioDTO;
import com.androidth.general.api.security.SecurityCompactDTOList;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
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
            @Path("providerId") int providerId);
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

    //<editor-fold desc="Get Cells">
    @GET("/providers/{providerId}/preSeason")
    Observable<CompetitionPreSeasonDTO> getPreseasonDetails(
            @Path("providerId") int providerId);
    //</editor-fold>

    //<editor-fold desc="Share Preseason">
    @POST("/social/prizeShare")
    Observable<BaseResponseDTO> sharePreseason(
            @Body CompetitionPreseasonShareFormDTO competitionPreseasonShareFormDTO);
    //</editor-fold>

    //<editor-fold desc="Get ProviderPrizePool">
    @GET("/providers/{providerId}/prizepool")
    Observable<ProviderPrizePoolDTO> getProviderPrizePool(@Path("providerId") int providerId);
    //</editor-fold>

    //<editor-fold desc="Get Provider Warrants">
    @GET("/providers/{providerId}/securities")
    Observable<SecurityCompactDTOList> getProviderWarrants(
            @Path("providerId") int providerId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            @Query("warrantType") String warrantShortCode);
    //</editor-fold>
}
