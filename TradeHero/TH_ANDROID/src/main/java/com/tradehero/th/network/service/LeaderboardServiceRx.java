package com.tradehero.th.network.service;

import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOList;
import com.tradehero.th.api.leaderboard.position.LeaderboardFriendsDTO;
import com.tradehero.th.api.position.GetPositionsDTO;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

interface LeaderboardServiceRx
{
    //<editor-fold desc="Get Leaderboard Definitions">
    @GET("/leaderboards")
    Observable<LeaderboardDefDTOList> getLeaderboardDefinitions();
    //</editor-fold>

    //<editor-fold desc="Get Leaderboard Definition">
    @GET("/leaderboards/{leaderboardId}/definition")
    Observable<LeaderboardDefDTO> getLeaderboardDef(
            @Path("leaderboardId") int leaderboardId);
    //</editor-fold>

    //<editor-fold desc="Get Leaderboard">
    @GET("/leaderboards/{leaderboardId}")
    Observable<LeaderboardDTO> getLeaderboard(
            @Path("leaderboardId") int leaderboardId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @Deprecated @GET("/leaderboards/{leaderboardId}")
    Observable<LeaderboardDTO> getLeaderboard(
            @Path("leaderboardId") Integer leaderboardId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            @Query("sortType") Integer sortType);
    //</editor-fold>

    //<editor-fold desc="Get Filtered Leaderboard">
    @GET("/filteredLeaderboards/{leaderboardId}")
    Observable<LeaderboardDTO> getFilteredLeaderboard(
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
    Observable<LeaderboardDTO> getUserOnLeaderboard(
            @Path("leaderboardId") int leaderboardId,
            @Path("userId") int userId,
            @Query("sortType") Integer sortType);
    //</editor-fold>

    //<editor-fold desc="Get Positions For Leaderboard Mark User">
    @GET("/leaderboardMarkUser/{leaderboardbMarkUserId}/positions")
    Observable<GetPositionsDTO> getPositionsForLeaderboardMarkUser(
            @Path("leaderboardbMarkUserId") int leaderboardbMarkUserId,
            @Query("pageNumber") Integer pageNumber,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Friends Leaderboard">
    @GET("/leaderboards/friends")
    Observable<LeaderboardDTO> getFriendsLeaderboard(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            @Query("includeFoF") Boolean includeFoF);

    @GET("/leaderboards/newfriends")
    Observable<LeaderboardFriendsDTO> getNewFriendsLeaderboard();

    /**
     *
     * @param page pagination parameter
     * @param sortType sort by HQ/ROI...
     * @param includeFoF whether to include second level friends to the leaderboard
     * @return
     */
    @Deprecated
    @GET("/leaderboards/friends")
    Observable<LeaderboardDTO> getFriendsLeaderboard(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            @Query("includeFoF") Boolean includeFoF,
            @Query("sortType") Integer sortType);
    //</editor-fold>
}
