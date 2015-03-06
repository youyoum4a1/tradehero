package com.tradehero.th.network.service;

import com.tradehero.chinabuild.data.UserCompetitionDTOList;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.CompetitionDTOList;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.tradehero.th.api.position.PositionDTO;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface CompetitionService
{
    //<editor-fold desc="Get Competitions">
    @GET("/providers/{providerId}/competitions") CompetitionDTOList getCompetitions(
            @Path("providerId") int providerId);
    //</editor-fold>

    //<editor-fold desc="Get Competition">
    @GET("/competitions/{competitionId}") CompetitionDTO getCompetition(
            @Path("competitionId") int competitionId);
    //</editor-fold>

    //<editor-fold desc="Get Competition Leaderboard">
    @GET("/providers/{providerId}/competitions/{competitionId}") CompetitionLeaderboardDTO getCompetitionLeaderboard(
            @Path("providerId") int providerId,
            @Path("competitionId") int competitionId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>


    //默认推荐比赛 api/usercompetitions/search?name=
    @GET("/usercompetitions/search?name=") UserCompetitionDTOList getRecommandCompetitions(
    );

    //用户创建比赛获取
    @GET("/usercompetitions?filterType=3&sortType=1") UserCompetitionDTOList getUserCompetitions(
            @Query("page") int page,
            @Query("perPage") int perPage
    );

    //官方比赛获取
    @GET("/usercompetitions?filterType=19&sortType=1") UserCompetitionDTOList getOfficalCompetitions(
            @Query("page") int page,
            @Query("perPage") int perPage
    );

    //我参与的比赛
    @GET("/usercompetitions?filterType=8&sortType=1") UserCompetitionDTOList getMyCompetitions(
            @Query("page") int page,
            @Query("perPage") int perPage
    );

    //官方推荐的比赛
    @GET("/usercompetitions?filterType=35&sortType=1") UserCompetitionDTOList getVipCompetitions(
            @Query("page") int page,
            @Query("perPage") int perPage
    );

    //搜索出来的比赛
    @GET("/usercompetitions/search?filterType=3") UserCompetitionDTOList getSearchCompetitions(
            @Query("name") String name,
            @Query("page") int page,
            @Query("perPage") int perPage
    );

    //我的比赛持仓
    @GET("/usercompetitions/{competitionId}/position") PositionDTO getPositionCompactDTO(
            @Path("competitionId") Integer competitionId,
            @Query("exchange") String exchange,
            @Query("symbol") String symbol
    );



}
