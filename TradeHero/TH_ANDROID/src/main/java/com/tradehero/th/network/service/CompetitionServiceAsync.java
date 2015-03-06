package com.tradehero.th.network.service;

import com.tradehero.chinabuild.data.UGCFromDTO;
import com.tradehero.chinabuild.data.UserCompetitionDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.timeline.TimelineDTO;
import retrofit.Callback;
import retrofit.http.*;

interface CompetitionServiceAsync
{
    @POST("/usercompetitions") void creatUGC(
            @Body UGCFromDTO form,
            Callback<UserCompetitionDTO> callback
    );

    @POST("/usercompetitions/enroll") void enrollUGCompetition(
            @Query("competitionId") int competitionId,
            Callback<UserCompetitionDTO> callback
    );

    @GET("/usercompetitions/{competitionId}/detail") void getCompetitionDetail(
            @Path("competitionId") int competitionId,
            Callback<UserCompetitionDTO> callback
    );

    //<editor-fold desc="Get MySelfRank">
    @GET("/leaderboards/{leaderboardsId}/users/{userId}") void getMySelfRank(
            @Path("leaderboardsId") int leaderboardsId,
            @Path("userId") int userId,
            Callback<LeaderboardDTO> callback);
    //</editor-fold>

    @GET("/discussions/competition/{competitionId}") void getCompetitionDiscuss(
            @Path("competitionId") int competitionId,
            @Query("page")int page,
            @Query("perPage")int perPage,
            Callback<TimelineDTO> callback);
}


