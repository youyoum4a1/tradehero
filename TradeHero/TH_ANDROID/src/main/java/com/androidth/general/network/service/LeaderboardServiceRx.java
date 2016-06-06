package com.androidth.general.network.service;

import com.androidth.general.api.leaderboard.LeaderboardDTO;
import com.androidth.general.api.leaderboard.def.LeaderboardDefDTO;
import com.androidth.general.api.leaderboard.def.LeaderboardDefDTOList;
import com.androidth.general.api.leaderboard.position.LeaderboardFriendsDTO;
import com.androidth.general.api.position.GetPositionsDTO;
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
            @Query("lbType") Integer lbType,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @Deprecated @GET("/leaderboards/{leaderboardId}")
    Observable<LeaderboardDTO> getLeaderboard(
            @Path("leaderboardId") Integer leaderboardId,
            @Query("lbType") Integer lbType,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            @Query("sortType") Integer sortType);
    //</editor-fold>

    //<editor-fold desc="Get Filtered Leaderboard">
    @GET("/filteredLeaderboards/{leaderboardId}")
    Observable<LeaderboardDTO> getFilteredLeaderboard(
            @Path("leaderboardId") int leaderboardId,
            @Query("lbType") Integer lbType,
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
            @Query("lbType") Integer lbType,
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
