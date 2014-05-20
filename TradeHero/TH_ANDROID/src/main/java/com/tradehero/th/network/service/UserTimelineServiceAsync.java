package com.tradehero.th.network.service;

import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

interface UserTimelineServiceAsync
{
    //<editor-fold desc="Get Global Timeline">
    @GET("/timeline")
    void getGlobalTimeline(
            Callback<TimelineDTO> callback);

    @GET("/timeline")
    void getGlobalTimeline(
            @Query("maxCount") int maxCount,
            Callback<TimelineDTO> callback);

    @GET("/timeline")
    void getGlobalTimeline(
            @Query("maxCount") int maxCount,
            @Query("maxId") Comparable maxId,
            Callback<TimelineDTO> callback);

    @GET("/timeline")
    void getGlobalTimeline(
            @Query("maxCount") int maxCount,
            @Query("maxId") Comparable maxId,
            @Query("minId") Comparable minId,
            Callback<TimelineDTO> callback);

    @GET("/timeline/{timelineId}") void getTimelineDetail(
            @Path("timelineId") int timelineId,
            Callback<TimelineItemDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Get User Timeline">
    @GET("/users/{userId}/timeline")
    void getTimeline(
            @Path("userId") int userId,
            Callback<TimelineDTO> callback);

    @GET("/users/{userId}/timeline")
    void getTimeline(
            @Path("userId") int userId,
            @Query("maxCount") int maxCount,
            Callback<TimelineDTO> callback);

    @GET("/users/{userId}/timeline")
    void getTimeline(
            @Path("userId") int userId,
            @Query("maxCount") int maxCount,
            @Query("maxId") Comparable maxId,
            Callback<TimelineDTO> callback);

    @GET("/users/{userId}/timeline")
    void getTimeline(
            @Path("userId") int userId,
            @Query("maxCount") int maxCount,
            @Query("maxId") Comparable maxId,
            @Query("minId") Comparable minId,
            Callback<TimelineDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Share Timeline Item">
    @POST("/users/{userId}/timeline/{timelineItemId}/share")
    void shareTimelineItem(
            @Path("userId") int userId,
            @Path("timelineItemId") int timelineItemId,
            @Body TimelineItemShareRequestDTO timelineItemShareRequestDTO,
            Callback<Response> callback);
    //</editor-fold>

    //<editor-fold desc="Delete Timeline Item">
    @DELETE("/users/{userId}/timeline/{timelineItemId}")
    void deleteTimelineItem(
            @Path("userId") int userId,
            @Path("timelineItemId") int timelineItemId,
            Callback<Response> callback);
    //</editor-fold>
}
