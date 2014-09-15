package com.tradehero.th.network.service;

import com.tradehero.common.billing.googleplay.Security;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.CompetitionDTOList;
import com.tradehero.th.api.competition.CompetitionFormDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.tradehero.th.api.position.PositionDTOCompact;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.chinabuild.data.UGCFromDTO;
import com.tradehero.th.fragments.chinabuild.data.UserCompetitionDTO;
import com.tradehero.th.fragments.chinabuild.data.UserCompetitionDTOList;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
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

    //<editor-fold desc="Enroll">
    @POST("/providers/enroll") UserProfileDTO enroll(
            @Body CompetitionFormDTO form);
    //</editor-fold>

    //<editor-fold desc="Outbound">
    @POST("/providers/outbound") UserProfileDTO outbound(
            @Body CompetitionFormDTO form);
    //</editor-fold>

    //创建UGC
    //POST https://tradehero.mobi/api/usercompetitions HTTP/1.1
    //Authorization: Basic amFja0B0cmFkZWhlcm8ubW9iaToxMTExMTE=
    //    Content-Type: application/json; charset=UTF-8
    //TH-Language-Code: zh-CN
    //Content-Length: 117
    //{"name":"不服来战","description":"进来看看到底谁是真正的大牛","durationDays":"10","exchangeIds":[4]}
    //@POST("/usercompetitions") Object creatUGC(
    //        @Body UGCFromDTO form
    //);

    //用户创建比赛获取
    @GET("/usercompetitions?filterType=1&sortType=1") UserCompetitionDTOList getUserCompetitions(
            @Query("page") int page,
            @Query("perPage") int perPage
    );

    //官方比赛获取
    @GET("/usercompetitions?page=1&perPage=20&filterType=19&sortType=1") UserCompetitionDTOList getOfficalCompetitions(
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
    @GET("/usercompetitions/search?filterType=1") UserCompetitionDTOList getSearchCompetitions(
            @Query("name") String name,
            @Query("page") int page,
            @Query("perPage") int perPage
    );

    //我的比赛持仓
    @GET("/usercompetitions/{competitionId}/position") PositionDTOCompact getPositionCompactDTO(
            @Path("competitionId") Integer competitionId,
            @Query("exchange") String exchange,
            @Query("symbol") String symbol
    );



}
