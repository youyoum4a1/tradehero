package com.tradehero.th.network.service;

import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.CompetitionDTOList;
import com.tradehero.th.api.competition.CompetitionFormDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.chinabuild.data.UGCFromDTO;
import com.tradehero.th.fragments.chinabuild.data.UserCompetitionDTO;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

interface CompetitionServiceAsync
{
    //<editor-fold desc="Get Competitions">
    @GET("/providers/{providerId}/competitions") void getCompetitions(
            @Path("providerId") int providerId,
            Callback<CompetitionDTOList> callback);
    //</editor-fold>

    //<editor-fold desc="Get Competition">
    @GET("/competitions/{competitionId}") void getCompetition(
            @Path("competitionId") int competitionId,
            Callback<CompetitionDTO> competitionDTOCallback);
    //</editor-fold>

    //<editor-fold desc="Get Competition Leaderboard">
    @GET("/providers/{providerId}/competitions/{competitionId}") void getCompetitionLeaderboard(
            @Path("providerId") int providerId,
            @Path("competitionId") int competitionId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            Callback<CompetitionLeaderboardDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Enroll">
    @POST("/providers/enroll") void enroll(
            @Body CompetitionFormDTO form,
            Callback<UserProfileDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Outbound">
    @POST("/providers/outbound") void outbound(
            @Body CompetitionFormDTO form,
            Callback<UserProfileDTO> callback);
    //</editor-fold>

    ////创建UGC
    ////POST https://tradehero.mobi/api/usercompetitions HTTP/1.1
    ////Authorization: Basic amFja0B0cmFkZWhlcm8ubW9iaToxMTExMTE=
    ////    Content-Type: application/json; charset=UTF-8
    ////TH-Language-Code: zh-CN
    ////Content-Length: 117
    ////{"name":"不服来战","description":"进来看看到底谁是真正的大牛","durationDays":"10","exchangeIds":[4]}
    ////@POST("/usercompetitions") Object creatUGC(
    ////        @Body UGCFromDTO form
    ////);
    //
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
}


