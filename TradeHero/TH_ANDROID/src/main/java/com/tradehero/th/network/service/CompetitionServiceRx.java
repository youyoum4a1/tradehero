package com.ayondo.academy.network.service;

import com.ayondo.academy.api.competition.CompetitionDTO;
import com.ayondo.academy.api.competition.CompetitionDTOList;
import com.ayondo.academy.api.leaderboard.competition.CompetitionLeaderboardDTO;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

interface CompetitionServiceRx
{
    //<editor-fold desc="Get Competitions">
    @GET("/providers/{providerId}/competitions")
    Observable<CompetitionDTOList> getCompetitions(
            @Path("providerId") int providerId);
    //</editor-fold>

    //<editor-fold desc="Get Competition">
    @GET("/competitions/{competitionId}")
    Observable<CompetitionDTO> getCompetition(
            @Path("competitionId") int competitionId);
    //</editor-fold>

    //<editor-fold desc="Get Competition Leaderboard">
    @GET("/providers/{providerId}/competitions/{competitionId}")
    Observable<CompetitionLeaderboardDTO> getCompetitionLeaderboard(
            @Path("providerId") int providerId,
            @Path("competitionId") int competitionId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>
}
