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
import com.androidth.general.api.security.SecurityCompositeDTO;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface ProviderServiceRx
{
    //<editor-fold desc="Get Providers">
    @GET("api/providers")
    Observable<ProviderDTOList> getProviders();
    //</editor-fold>

    @GET("api/providers/redeem/{providerId}/{code}")
    Observable<String> validatedRedeemCode(
            @Path("providerId") Integer providerId,
            @Path("code") String typedCode);

    //<editor-fold desc="Get Provider">
    @GET("api/providers/{providerId}")
    Observable<ProviderDTO> getProvider(@Path("providerId") int providerId);
    //</editor-fold>

    //<editor-fold desc="Get Provider Portfolio">
    @GET("api/providers/{providerId}/portfolio")
    Observable<PortfolioDTO> getPortfolio(
            @Path("providerId") int providerId);
    //</editor-fold>

    //<editor-fold desc="Get Provider Securities">
    @GET("api/providers/{providerId}/securities")
    Observable<SecurityCompactDTOList> getSecurities(
            @Path("providerId") int providerId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Provider Securities">
    @GET("api/providers/{providerId}/securitiesv2")
    Observable<SecurityCompositeDTO> getSecuritiesV2(
            @Path("providerId") int providerId);
    //</editor-fold>

    //<editor-fold desc="Get Provider Warrant Underlyers">
    @GET("api/providers/{providerId}/warrantUnderlyers")
    Observable<SecurityCompactDTOList> getWarrantUnderlyers(
            @Path("providerId") int providerId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Search Provider Securities">
    @GET("api/providers/{providerId}/securities")
    Observable<SecurityCompactDTOList> searchSecurities(
            @Path("providerId") int providerId,
            @Query("q") String searchString,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Help Videos">
    @GET("api/providers/{providerId}/helpVideos")
    Observable<HelpVideoDTOList> getHelpVideos(
            @Path("providerId") int providerId);
    //</editor-fold>

    //<editor-fold desc="Get Cells">
    @GET("api/providers/{providerId}/displaycells")
    Observable<ProviderDisplayCellDTOList> getDisplayCells(
            @Path("providerId") int providerId);
    //</editor-fold>

    //<editor-fold desc="Get Cells">
    @GET("api/providers/{providerId}/preSeason")
    Observable<CompetitionPreSeasonDTO> getPreseasonDetails(
            @Path("providerId") int providerId);
    //</editor-fold>

    //<editor-fold desc="Share Preseason">
    @POST("api/social/prizeShare")
    Observable<BaseResponseDTO> sharePreseason(
            @Body CompetitionPreseasonShareFormDTO competitionPreseasonShareFormDTO);
    //</editor-fold>

    //<editor-fold desc="Get ProviderPrizePool">
    @GET("api/providers/{providerId}/prizepool")
    Observable<ProviderPrizePoolDTO> getProviderPrizePool(@Path("providerId") int providerId);
    //</editor-fold>

    //<editor-fold desc="Get Provider Warrants">
    @GET("api/providers/{providerId}/securities")
    Observable<SecurityCompactDTOList> getProviderWarrants(
            @Path("providerId") int providerId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            @Query("warrantType") String warrantShortCode);
    //</editor-fold>
}
