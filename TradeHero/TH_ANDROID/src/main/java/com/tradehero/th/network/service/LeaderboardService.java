package com.tradehero.th.network.service;

import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.position.GetLeaderboardPositionsDTO;
import com.tradehero.th.api.position.GetPositionsDTO;
import java.util.List;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/** Created with IntelliJ IDEA. User: tho Date: 10/14/13 Time: 3:35 PM Copyright (c) TradeHero */
public interface LeaderboardService
{
    //<editor-fold desc="Get Leaderboard Definitions">
    @GET("/leaderboards")
    List<LeaderboardDefDTO> getLeaderboardDefinitions()
            throws RetrofitError;

    @GET("/leaderboards")
    void getLeaderboardDefinitions(Callback<List<LeaderboardDefDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="Get Leaderboard">
    @GET("/leaderboards/{leaderboardId}")
    LeaderboardDTO getLeaderboard(
            @Path("leaderboardId") Integer leaderboardId)
        throws RetrofitError;

    @GET("/leaderboards/{leaderboardId}")
    void getLeaderboard(
            @Path("leaderboardId") Integer leaderboardId,
            Callback<LeaderboardDTO> callback);

    @GET("/leaderboards/{leaderboardId}")
    LeaderboardDTO getLeaderboard(
            @Path("leaderboardId") Integer leaderboardId,
            @Query("page") Integer page)
        throws RetrofitError;

    @GET("/leaderboards/{leaderboardId}")
    void getLeaderboard(
            @Path("leaderboardId") Integer leaderboardId,
            @Query("page") Integer page,
            Callback<LeaderboardDTO> callback);

    @GET("/leaderboards/{leaderboardId}")
    LeaderboardDTO getLeaderboard(
            @Path("leaderboardId") Integer leaderboardId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage)
        throws RetrofitError;

    @GET("/leaderboards/{leaderboardId}")
    void getLeaderboard(
            @Path("leaderboardId") Integer leaderboardId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            Callback<LeaderboardDTO> callback);

    @Deprecated
    @GET("/leaderboards/{leaderboardId}")
    LeaderboardDTO getLeaderboard(
            @Path("leaderboardId") Integer leaderboardId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            @Query("sortType") Integer sortType)
        throws RetrofitError;

    @Deprecated
    @GET("/leaderboards/{leaderboardId}")
    void getLeaderboard(
            @Path("leaderboardId") Integer leaderboardId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            @Query("sortType") Integer sortType,
            Callback<LeaderboardDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Get Filtered Leaderboard">
    @GET("/filteredLeaderboards/{leaderboardId}")
    LeaderboardDTO getFilteredLeaderboard(
            @Path("leaderboardId") Integer leaderboardId,
            @Query("winRatio") Float winRatio,
            @Query("avgMonthlyTradeCount") Float averageMonthlyTradeCount,
            @Query("avgHoldingDays") Float averageHoldingDays,
            @Query("minSharpeRatio") Float minSharpeRatio,
            @Query("maxPosRoiVolatility") Float maxPosRoiVolatility)
        throws RetrofitError;

    @GET("/filteredLeaderboards/{leaderboardId}")
    void getFilteredLeaderboard(
            @Path("leaderboardId") Integer leaderboardId,
            @Query("winRatio") Float winRatio,
            @Query("avgMonthlyTradeCount") Float averageMonthlyTradeCount,
            @Query("avgHoldingDays") Float averageHoldingDays,
            @Query("minSharpeRatio") Float minSharpeRatio,
            @Query("maxPosRoiVolatility") Float maxPosRoiVolatility,
            Callback<LeaderboardDTO> callback);

    @GET("/filteredLeaderboards/{leaderboardId}")
    LeaderboardDTO getFilteredLeaderboard(
            @Path("leaderboardId") Integer leaderboardId,
            @Query("winRatio") Float winRatio,
            @Query("avgMonthlyTradeCount") Float averageMonthlyTradeCount,
            @Query("avgHoldingDays") Float averageHoldingDays,
            @Query("minSharpeRatio") Float minSharpeRatio,
            @Query("maxPosRoiVolatility") Float maxPosRoiVolatility,
            @Query("page") Integer page)
        throws RetrofitError;

    @GET("/filteredLeaderboards/{leaderboardId}")
    void getFilteredLeaderboard(
            @Path("leaderboardId") Integer leaderboardId,
            @Query("winRatio") Float winRatio,
            @Query("avgMonthlyTradeCount") Float averageMonthlyTradeCount,
            @Query("avgHoldingDays") Float averageHoldingDays,
            @Query("minSharpeRatio") Float minSharpeRatio,
            @Query("maxPosRoiVolatility") Float maxPosRoiVolatility,
            @Query("page") Integer page,
            Callback<LeaderboardDTO> callback);

    @GET("/filteredLeaderboards/{leaderboardId}")
    LeaderboardDTO getFilteredLeaderboard(
            @Path("leaderboardId") Integer leaderboardId,
            @Query("winRatio") Float winRatio,
            @Query("avgMonthlyTradeCount") Float averageMonthlyTradeCount,
            @Query("avgHoldingDays") Float averageHoldingDays,
            @Query("minSharpeRatio") Float minSharpeRatio,
            @Query("maxPosRoiVolatility") Float maxPosRoiVolatility,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage)
        throws RetrofitError;

    @GET("/filteredLeaderboards/{leaderboardId}")
    void getFilteredLeaderboard(
            @Path("leaderboardId") Integer leaderboardId,
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
    LeaderboardDTO getUserOnLeaderboard(
            @Path("leaderboardId") int leaderboardId,
            @Path("userId") int userId)
        throws RetrofitError;

    @GET("/leaderboards/{leaderboardId}/users/{userId}")
    void getUserOnLeaderboard(
            @Path("leaderboardId") int leaderboardId,
            @Path("userId") int userId,
            Callback<LeaderboardDTO> callback);

    @Deprecated
    @GET("/leaderboards/{leaderboardId}/users/{userId}")
    LeaderboardDTO getUserOnLeaderboard(
            @Path("leaderboardId") int leaderboardId,
            @Path("userId") int userId,
            @Query("sortType") int sortType)
        throws RetrofitError;

    @Deprecated
    @GET("/leaderboards/{leaderboardId}/users/{userId}")
    void getUserOnLeaderboard(
            @Path("leaderboardId") int leaderboardId,
            @Path("userId") int userId,
            @Query("sortType") int sortType,
            Callback<LeaderboardDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Get Positions For Leaderboard Mark User">
    @GET("/leaderboardMarkUser/{leaderboardbMarkUserId}/positions")
    GetLeaderboardPositionsDTO getPositionsForLeaderboardMarkUser(
            @Path("leaderboardbMarkUserId") int leaderboardbMarkUserId)
        throws RetrofitError;

    @GET("/leaderboardMarkUser/{leaderboardbMarkUserId}/positions")
    void getPositionsForLeaderboardMarkUser(
            @Path("leaderboardbMarkUserId") int leaderboardbMarkUserId,
            Callback<GetLeaderboardPositionsDTO> callback);

    @GET("/leaderboardMarkUser/{leaderboardbMarkUserId}/positions")
    GetLeaderboardPositionsDTO getPositionsForLeaderboardMarkUser(
            @Path("leaderboardbMarkUserId") int leaderboardbMarkUserId,
            @Query("pageNumber") int pageNumber)
        throws RetrofitError;

    @GET("/leaderboardMarkUser/{leaderboardbMarkUserId}/positions")
    void getPositionsForLeaderboardMarkUser(
            @Path("leaderboardbMarkUserId") int leaderboardbMarkUserId,
            @Query("pageNumber") int pageNumber,
            Callback<GetLeaderboardPositionsDTO> callback);

    @GET("/leaderboardMarkUser/{leaderboardbMarkUserId}/positions")
    GetLeaderboardPositionsDTO getPositionsForLeaderboardMarkUser(
            @Path("leaderboardbMarkUserId") int leaderboardbMarkUserId,
            @Query("pageNumber") int pageNumber,
            @Query("perPage") int perPage)
        throws RetrofitError;

    @GET("/leaderboardMarkUser/{leaderboardbMarkUserId}/positions")
    void getPositionsForLeaderboardMarkUser(
            @Path("leaderboardbMarkUserId") int leaderboardbMarkUserId,
            @Query("pageNumber") int pageNumber,
            @Query("perPage") int perPage,
            Callback<GetLeaderboardPositionsDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Get Friends Leaderboard">
    @GET("/leaderboards/friends")
    LeaderboardDTO getFriendsLeaderboard()
            throws RetrofitError;

    @GET("/leaderboards/friends")
    void getFriendsLeaderboard(Callback<LeaderboardDTO> callback);

    @GET("/leaderboards/friends")
    LeaderboardDTO getFriendsLeaderboard(
            @Query("page") Integer page)
            throws RetrofitError;

    @GET("/leaderboards/friends")
    void getFriendsLeaderboard(
            @Query("page") Integer page,
            Callback<LeaderboardDTO> callback);

    @GET("/leaderboards/friends")
    LeaderboardDTO getFriendsLeaderboard(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage)
            throws RetrofitError;

    @GET("/leaderboards/friends")
    void getFriendsLeaderboard(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            Callback<LeaderboardDTO> callback);

    @GET("/leaderboards/friends")
    LeaderboardDTO getFriendsLeaderboard(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            @Query("includeFoF") Boolean includeFoF)
            throws RetrofitError;

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
     * @throws RetrofitError
     */
    @Deprecated
    @GET("/leaderboards/friends")
    LeaderboardDTO getFriendsLeaderboard(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            @Query("includeFoF") Boolean includeFoF,
            @Query("sortType") Integer sortType)
        throws RetrofitError;

    @Deprecated
    @GET("/leaderboards/friends")
    void getFriendsLeaderboard(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage,
            @Query("includeFoF") Boolean includeFoF,
            @Query("sortType") Integer sortType,
            Callback<LeaderboardDTO> callback);
    //</editor-fold>
}
