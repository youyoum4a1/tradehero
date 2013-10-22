package com.tradehero.th.network.service;

import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
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
            @Path("leaderboardId") Integer lbId)
        throws RetrofitError;

    @GET("/leaderboards/{leaderboardId}")
    void getLeaderboard(
            @Path("leaderboardId") Integer lbId,
            Callback<LeaderboardDTO> callback);

    @GET("/leaderboards/{leaderboardId}")
    LeaderboardDTO getLeaderboard(
            @Path("leaderboardId") Integer lbId,
            @Query("path") Integer page)
        throws RetrofitError;

    @GET("/leaderboards/{leaderboardId}")
    void getLeaderboard(
            @Path("leaderboardId") Integer lbId,
            @Query("path") Integer page,
            Callback<LeaderboardDTO> callback);

    @GET("/leaderboards/{leaderboardId}")
    LeaderboardDTO getLeaderboard(
            @Path("leaderboardId") Integer lbId,
            @Query("path") Integer page,
            @Query("perPage") Integer perPage)
        throws RetrofitError;

    @GET("/leaderboards/{leaderboardId}")
    void getLeaderboard(
            @Path("leaderboardId") Integer lbId,
            @Query("path") Integer page,
            @Query("perPage") Integer perPage,
            Callback<LeaderboardDTO> callback);

    @GET("/leaderboards/{leaderboardId}")
    LeaderboardDTO getLeaderboard(
            @Path("leaderboardId") Integer lbId,
            @Query("path") Integer page,
            @Query("perPage") Integer perPage,
            @Query("sortType") Integer sortType)
        throws RetrofitError;

    @GET("/leaderboards/{leaderboardId}")
    void getLeaderboard(
            @Path("leaderboardId") Integer lbId,
            @Query("path") Integer page,
            @Query("perPage") Integer perPage,
            @Query("sortType") Integer sortType,
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
            Callback<LeaderboardDTO> callback)
        throws RetrofitError;

    @GET("/leaderboards/{leaderboardId}/users/{userId}")
    LeaderboardDTO getUserOnLeaderboard(
            @Path("leaderboardId") int leaderboardId,
            @Path("userId") int userId,
            @Query("sortType") int sortType)
        throws RetrofitError;

    @GET("/leaderboards/{leaderboardId}/users/{userId}")
    void getUserOnLeaderboard(
            @Path("leaderboardId") int leaderboardId,
            @Path("userId") int userId,
            @Query("sortType") int sortType,
            Callback<LeaderboardDTO> callback)
        throws RetrofitError;
    //</editor-fold>

    //<editor-fold desc="Get Positions For Leaderboard Mark User">
    @GET("/leaderboardMarkUser/{leaderboardbMarkUserId}/positions")
    GetPositionsDTO getPositionsForLeaderboardMarkUser(
            @Path("leaderboardbMarkUserId") int leaderboardbMarkUserId)
        throws RetrofitError;

    @GET("/leaderboardMarkUser/{leaderboardbMarkUserId}/positions")
    void getPositionsForLeaderboardMarkUser(
            @Path("leaderboardbMarkUserId") int leaderboardbMarkUserId,
            Callback<GetPositionsDTO> callback);

    @GET("/leaderboardMarkUser/{leaderboardbMarkUserId}/positions")
    GetPositionsDTO getPositionsForLeaderboardMarkUser(
            @Path("leaderboardbMarkUserId") int leaderboardbMarkUserId,
            @Query("pageNumber") int pageNumber)
        throws RetrofitError;

    @GET("/leaderboardMarkUser/{leaderboardbMarkUserId}/positions")
    void getPositionsForLeaderboardMarkUser(
            @Path("leaderboardbMarkUserId") int leaderboardbMarkUserId,
            @Query("pageNumber") int pageNumber,
            Callback<GetPositionsDTO> callback);

    @GET("/leaderboardMarkUser/{leaderboardbMarkUserId}/positions")
    GetPositionsDTO getPositionsForLeaderboardMarkUser(
            @Path("leaderboardbMarkUserId") int leaderboardbMarkUserId,
            @Query("pageNumber") int pageNumber,
            @Query("perPage") int perPage)
        throws RetrofitError;

    @GET("/leaderboardMarkUser/{leaderboardbMarkUserId}/positions")
    void getPositionsForLeaderboardMarkUser(
            @Path("leaderboardbMarkUserId") int leaderboardbMarkUserId,
            @Query("pageNumber") int pageNumber,
            @Query("perPage") int perPage,
            Callback<GetPositionsDTO> callback);
    //</editor-fold>
}
