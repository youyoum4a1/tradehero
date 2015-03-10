package com.tradehero.th.network.service;

import com.tradehero.chinabuild.data.CompetitionDescription;
import com.tradehero.chinabuild.data.UGCFromDTO;
import com.tradehero.chinabuild.data.UserCompetitionDTO;
import com.tradehero.chinabuild.data.UserCompetitionDTOList;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
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

    @GET("/usercompetitions?filterType=10&sortType=1") void getMyOpenCompetitions(Callback<UserCompetitionDTOList> callback);

    @GET("/usercompetitions?filterType=136&sortType=5") void getMyClosedCompetitions(
            @Query("perPage") int perPage,
            @Query("page") int page,
            Callback<UserCompetitionDTOList> callback);

    @PUT("/usercompetitions/{competitionId}") void updateCompetitionDescription(
            @Path("competitionId") int competitionId,
            @Body CompetitionDescription description,
            Callback<UserCompetitionDTO> callback);
}


