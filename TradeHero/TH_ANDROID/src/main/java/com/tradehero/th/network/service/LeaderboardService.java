package com.tradehero.th.network.service;

import com.tradehero.chinabuild.data.UserTrendingDTO;
import com.tradehero.chinabuild.data.UserTrendingDTOList;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOList;
import com.tradehero.th.api.leaderboard.position.LeaderboardFriendsDTO;
import com.tradehero.th.api.position.GetPositionsDTO;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface LeaderboardService
{
    //<editor-fold desc="Get Leaderboard Definitions">
    @GET("/leaderboards")
    LeaderboardDefDTOList getLeaderboardDefinitions();
    //</editor-fold>

    //月盈利榜
    //<editor-fold desc="Get Leaderboard">
    @GET("/leaderboards/{leaderboardId}")
    LeaderboardDTO getLeaderboard(
            @Path("leaderboardId") int leaderboardId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    //整合搜索默认推荐榜单
    @GET("/users/trendingSearch")
    UserTrendingDTOList getLeaderboardSearchRecommend();


    //活跃ROI榜 推荐榜
    //<editor-fold desc="Get Leaderboard">
    @Deprecated @GET("/users/trendingPerfRoi?countryCode=CN")
    UserTrendingDTOList getLeaderboardDayROI(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    // 推荐榜
    @GET("/cn/v2/users/trendingPerfRoi")
    UserTrendingDTOList getLeaderboardPrefROI(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage
    );

    //高胜率榜
    @GET("/cn/v2/users/trendingWinRatio")
    UserTrendingDTOList getLeaderboardWinRatio(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    //人气榜
    //<editor-fold desc="Get Leaderboard">
    @GET("/users/trendingFollow?countryCode=CN")
    UserTrendingDTOList getLeaderboardPopular(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    //土豪榜
    //<editor-fold desc="Get Leaderboard">
    @GET("/users/trendingWealth?countryCode=CN")
    UserTrendingDTOList getLeaderboardWealth(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @Deprecated @GET("/leaderboards/{leaderboardId}")
    LeaderboardDTO getLeaderboard(
            @Path("leaderboardId") Integer leaderboardId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            @Query("sortType") Integer sortType);
    //</editor-fold>

    //<editor-fold desc="Get Filtered Leaderboard">
    @GET("/filteredLeaderboards/{leaderboardId}")
    LeaderboardDTO getFilteredLeaderboard(
            @Path("leaderboardId") int leaderboardId,
            @Query("winRatio") Float winRatio,
            @Query("avgMonthlyTradeCount") Float averageMonthlyTradeCount,
            @Query("avgHoldingDays") Float averageHoldingDays,
            @Query("minSharpeRatio") Float minSharpeRatio,
            @Query("maxPosRoiVolatility") Float maxPosRoiVolatility,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get User On Leaderboard">
    @GET("/leaderboards/{leaderboardId}/users/{userId}")
    LeaderboardDTO getUserOnLeaderboard(
            @Path("leaderboardId") int leaderboardId,
            @Path("userId") int userId,
            @Query("sortType") Integer sortType);
    //</editor-fold>

    //<editor-fold desc="Get Positions For Leaderboard Mark User">
    @GET("/leaderboardMarkUser/{leaderboardbMarkUserId}/positions")
    GetPositionsDTO getPositionsForLeaderboardMarkUser(
            @Path("leaderboardbMarkUserId") int leaderboardbMarkUserId,
            @Query("pageNumber") Integer pageNumber,
            @Query("perPage") Integer perPage);
    //</editor-fold>


    @GET("/leaderboards/newfriends")
    LeaderboardFriendsDTO getNewFriendsLeaderboard();

}
