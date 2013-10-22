package com.tradehero.th.network.service;

import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import com.tradehero.th.api.timeline.TimelineReadDTO;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/** Created with IntelliJ IDEA. User: tho Date: 9/3/13 Time: 12:47 PM Copyright (c) TradeHero */
public interface UserTimelineService
{
    //<editor-fold desc="Get Global Timeline">
    @GET("/timeline")
    TimelineDTO getGlobalTimeline()
        throws RetrofitError;

    @GET("/timeline")
    void getGlobalTimeline(
            Callback<TimelineDTO> callback);

    @GET("/timeline")
    TimelineDTO getGlobalTimeline(
            @Query("maxCount") int maxCount)
        throws RetrofitError;

    @GET("/timeline")
    void getGlobalTimeline(
            @Query("maxCount") int maxCount,
            Callback<TimelineDTO> callback);

    @GET("/timeline")
    TimelineDTO getGlobalTimeline(
            @Query("maxCount") int maxCount,
            @Query("maxId") Comparable maxId)
        throws RetrofitError;

    @GET("/timeline")
    void getGlobalTimeline(
            @Query("maxCount") int maxCount,
            @Query("maxId") Comparable maxId,
            Callback<TimelineDTO> callback);

    @GET("/timeline")
    TimelineDTO getGlobalTimeline(
            @Query("maxCount") int maxCount,
            @Query("maxId") Comparable maxId,
            @Query("minId") Comparable minId)
        throws RetrofitError;

    @GET("/timeline")
    void getGlobalTimeline(
            @Query("maxCount") int maxCount,
            @Query("maxId") Comparable maxId,
            @Query("minId") Comparable minId,
            Callback<TimelineDTO> callback);
    //</editor-fold>

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
    TimelineItemDTO getTimelineItem(
            @Path("userId") int userId,
            @Path("timelineItemId") int timelineItemId)
            throws RetrofitError;

    @GET("/users/{userId}/timeline/{timelineItemId}")
    void getTimelineItem(
            @Path("userId") int userId,
            @Path("timelineItemId") int timelineItemId,
            Callback<TimelineItemDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Share Timeline Item">
    @POST("/users/{userId}/timeline/{timelineItemId}/share")
    Response shareTimelineItem( // Appears to return a boolean
            @Path("userId") int userId,
            @Path("timelineItemId") int timelineItemId,
            @Body TimelineItemShareRequestDTO timelineItemShareRequestDTO)
        throws RetrofitError;

    @POST("/users/{userId}/timeline/{timelineItemId}/share")
    void shareTimelineItem(
            @Path("userId") int userId,
            @Path("timelineItemId") int timelineItemId,
            @Body TimelineItemShareRequestDTO timelineItemShareRequestDTO,
            Callback<Response> callback);
    //</editor-fold>

    //<editor-fold desc="Delete Timeline Item">
    @DELETE("/users/{userId}/timeline/{timelineItemId}")
    TimelineItemDTO deleteTimelineItem(
            @Path("userId") int userId,
            @Path("timelineItemId") int timelineItemId)
        throws RetrofitError;

    @DELETE("/users/{userId}/timeline/{timelineItemId}")
    void deleteTimelineItem(
            @Path("userId") int userId,
            @Path("timelineItemId") int timelineItemId,
            Callback<TimelineItemDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Post Timeline Marker">
    @POST("/users/{userId}/read")
    TimelineReadDTO postTimelineMarker(
            @Path("userId") int userId,
            @Body TimelineReadDTO lastReadDTO)
        throws RetrofitError;

    @POST("/users/{userId}/read")
    void postTimelineMarker(
            @Path("userId") int userId,
            @Body TimelineReadDTO lastReadDTO,
            Callback<TimelineReadDTO> callback);
    //</editor-fold>
}
