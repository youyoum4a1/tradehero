package com.tradehero.th.network.service;

import com.tradehero.th.api.competition.CompetitionDTOList;
import com.tradehero.th.api.competition.CompetitionFormDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface CompetitionService
{
    //<editor-fold desc="Get Competitions">
    @GET("/providers/{providerId}/competitions")
    CompetitionDTOList getCompetitions(
            @Path("providerId") int providerId);
    //</editor-fold>

    //<editor-fold desc="Get Competition Leaderboard">
    @GET("/providers/{providerId}/competitions/{competitionId}")
    CompetitionLeaderboardDTO getCompetitionLeaderboard(
            @Path("providerId") int providerId,
            @Path("competitionId") int competitionId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Enroll">
    @POST("/providers/enroll")
    UserProfileDTO enroll(
            @Body CompetitionFormDTO form);
    //</editor-fold>

    //<editor-fold desc="Outbound">
    @POST("/providers/outbound")
    UserProfileDTO outbound(
            @Body CompetitionFormDTO form);
    //</editor-fold>
}
