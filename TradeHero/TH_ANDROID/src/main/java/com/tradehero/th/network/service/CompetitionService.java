package com.tradehero.th.network.service;

import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.CompetitionFormDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import java.util.List;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;


public interface CompetitionService
{
    //<editor-fold desc="Get Competitions">
    @GET("/providers/{providerId}/competitions")
    List<CompetitionDTO> getCompetitions(
            @Path("providerId") int providerId)
        throws RetrofitError;

    @GET("/providers/{providerId}/competitions")
    void getCompetitions(
            @Path("providerId") int providerId,
            Callback<List<CompetitionDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="Get Competition Leaderboard">
    @GET("/providers/{providerId}/competitions/{competitionId}")
    CompetitionLeaderboardDTO getCompetitionLeaderboard(
            @Path("providerId") int providerId,
            @Path("competitionId") int competitionId)
        throws RetrofitError;

    @GET("/providers/{providerId}/competitions/{competitionId}")
    void getCompetitionLeaderboard(
            @Path("providerId") int providerId,
            @Path("competitionId") int competitionId,
            Callback<CompetitionLeaderboardDTO> callback);

    @GET("/providers/{providerId}/competitions/{competitionId}")
    CompetitionLeaderboardDTO getCompetitionLeaderboard(
            @Path("providerId") int providerId,
            @Path("competitionId") int competitionId,
            @Query("page") int page)
        throws RetrofitError;

    @GET("/providers/{providerId}/competitions/{competitionId}")
    void getCompetitionLeaderboard(
            @Path("providerId") int providerId,
            @Path("competitionId") int competitionId,
            @Query("page") int page,
            Callback<CompetitionLeaderboardDTO> callback);

    @GET("/providers/{providerId}/competitions/{competitionId}")
    CompetitionLeaderboardDTO getCompetitionLeaderboard(
            @Path("providerId") int providerId,
            @Path("competitionId") int competitionId,
            @Query("page") int page,
            @Query("perPage") int perPage)
        throws RetrofitError;

    @GET("/providers/{providerId}/competitions/{competitionId}")
    void getCompetitionLeaderboard(
            @Path("providerId") int providerId,
            @Path("competitionId") int competitionId,
            @Query("page") int page,
            @Query("perPage") int perPage,
            Callback<CompetitionLeaderboardDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Enroll">
    @POST("/providers/enroll")
    UserProfileDTO enroll(
            @Body CompetitionFormDTO form)
            throws RetrofitError;

    @POST("/providers/enroll")
    void enroll(
            @Body CompetitionFormDTO form,
            Callback<UserProfileDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Outbound">
    @POST("/providers/outbound")
    UserProfileDTO outbound(
            @Body CompetitionFormDTO form)
            throws RetrofitError;

    @POST("/providers/outbound")
    void outbound(
            @Body CompetitionFormDTO form,
            Callback<UserProfileDTO> callback);
    //</editor-fold>
}
