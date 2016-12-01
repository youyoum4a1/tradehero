package com.androidth.general.network.service;

import com.androidth.general.api.competition.CompetitionDTO;
import com.androidth.general.api.competition.CompetitionDTOList;
import com.androidth.general.api.leaderboard.competition.CompetitionLeaderboardDTO;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

interface CompetitionServiceRx
{
    //<editor-fold desc="Get Competitions">
    @GET("api/providers/{providerId}/competitions")
    Observable<CompetitionDTOList> getCompetitions(
            @Path("providerId") int providerId);
    //</editor-fold>

    //<editor-fold desc="Get Competition">
    @GET("api/competitions/{competitionId}")
    Observable<CompetitionDTO> getCompetition(
            @Path("competitionId") int competitionId);
    //</editor-fold>

    //<editor-fold desc="Get Competition Leaderboard">
    @GET("api/providers/{providerId}/competitions/{competitionId}")
    Observable<CompetitionLeaderboardDTO> getCompetitionLeaderboard(
            @Path("providerId") int providerId,
            @Path("competitionId") int competitionId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>
}
