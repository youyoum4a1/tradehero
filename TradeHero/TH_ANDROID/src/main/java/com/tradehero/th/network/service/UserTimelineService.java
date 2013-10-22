package com.tradehero.th.network.service;

import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/** Created with IntelliJ IDEA. User: tho Date: 9/3/13 Time: 12:47 PM Copyright (c) TradeHero */
public interface UserTimelineService
{
    //<editor-fold desc="Get User Timeline">
    @GET("/users/{userId}/timeline")
    TimelineDTO getTimeline(
            @Path("userId") int userId)
            throws RetrofitError;

    @GET("/users/{userId}/timeline")
    void getTimeline(
            @Path("userId") int userId,
            Callback<TimelineDTO> callback);

    @GET("/users/{userId}/timeline")
    TimelineDTO getTimeline(
            @Path("userId") int userId,
            @Query("maxCount") int maxCount)
            throws RetrofitError;

    @GET("/users/{userId}/timeline")
    void getTimeline(
            @Path("userId") int userId,
            @Query("maxCount") int maxCount,
            Callback<TimelineDTO> callback);

    @GET("/users/{userId}/timeline")
    TimelineDTO getTimeline(
            @Path("userId") int userId,
            @Query("maxCount") int maxCount,
            @Query("maxId") Comparable maxId)
            throws RetrofitError;

    @GET("/users/{userId}/timeline")
    void getTimeline(
            @Path("userId") int userId,
            @Query("maxCount") int maxCount,
            @Query("maxId") Comparable maxId,
            Callback<TimelineDTO> callback);

    @GET("/users/{userId}/timeline")
    TimelineDTO getTimeline(
            @Path("userId") int userId,
            @Query("maxCount") int maxCount,
            @Query("maxId") Comparable maxId,
            @Query("minId") Comparable minId)
            throws RetrofitError;

    @GET("/users/{userId}/timeline")
    void getTimeline(
            @Path("userId") int userId,
            @Query("maxCount") int maxCount,
            @Query("maxId") Comparable maxId,
            @Query("minId") Comparable minId,
            Callback<TimelineDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Get Timeline Item">
    @GET("/users/{userId}/timeline/{timelineItemId}")
    void getTimelineItem(
            @Path("userId") int userId,
            @Path("timelineItemId") int timelineItemId,
            Callback<TimelineItemDTO> callback);

    @GET("/users/{userId}/timeline/{timelineItemId}")
    TimelineItemDTO getTimelineItem(
            @Path("userId") int userId,
            @Path("timelineItemId") int timelineItemId)
            throws RetrofitError;
    //</editor-fold>
}
