package com.tradehero.th.network.service;

import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.position.GetPositionsDTO;
import java.util.List;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

interface LeaderboardServiceAsync
{
    //<editor-fold desc="Get Leaderboard Definitions">
    @GET("/leaderboards")
    void getLeaderboardDefinitions(Callback<List<LeaderboardDefDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="Get Leaderboard">
    @GET("/leaderboards/{leaderboardId}")
    void getLeaderboard(
            @Path("leaderboardId") int leaderboardId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            Callback<LeaderboardDTO> callback);

    @Deprecated
    @GET("/leaderboards/{leaderboardId}")
    void getLeaderboard(
            @Path("leaderboardId") int leaderboardId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            @Query("sortType") Integer sortType,
            Callback<LeaderboardDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Get Filtered Leaderboard">
    @GET("/filteredLeaderboards/{leaderboardId}")
    void getFilteredLeaderboard(
            @Path("leaderboardId") int leaderboardId,
            @Query("winRatio") Float winRatio,
            @Query("avgMonthlyTradeCount") Float averageMonthlyTradeCount,
            @Query("avgHoldingDays") Float averageHoldingDays,
            @Query("minSharpeRatio") Float minSharpeRatio,
            @Query("maxPosRoiVolatility") Float maxPosRoiVolatility,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            Callback<LeaderboardDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Get User On Leaderboard">
    @GET("/leaderboards/{leaderboardId}/users/{userId}")
    void getUserOnLeaderboard(
            @Path("leaderboardId") int leaderboardId,
            @Path("userId") int userId,
            Callback<LeaderboardDTO> callback);

    @Deprecated @GET("/leaderboards/{leaderboardId}/users/{userId}")
    void getUserOnLeaderboard(
            @Path("leaderboardId") int leaderboardId,
            @Path("userId") int userId,
            @Query("sortType") Integer sortType,
            Callback<LeaderboardDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Get Positions For Leaderboard Mark User">
    @GET("/leaderboardMarkUser/{leaderboardbMarkUserId}/positions")
    void getPositionsForLeaderboardMarkUser(
            @Path("leaderboardbMarkUserId") int leaderboardbMarkUserId,
            @Query("pageNumber") Integer pageNumber,
            @Query("perPage") Integer perPage,
            Callback<GetPositionsDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Get Friends Leaderboard">
    @GET("/leaderboards/friends")
    void getFriendsLeaderboard(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            @Query("includeFoF") Boolean includeFoF,
            Callback<LeaderboardDTO> callback);

    /**
     *
     * @param page pagination parameter
     * @param sortType sort by HQ/ROI...
     * @param includeFoF whether to include second level friends to the leaderboard
     * @return
     * @throws retrofit.RetrofitError
     */
    @GET("/leaderboards/friends")
    void getFriendsLeaderboard(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            @Query("includeFoF") Boolean includeFoF,
            @Query("sortType") Integer sortType,
            Callback<LeaderboardDTO> callback);
    //</editor-fold>
}
