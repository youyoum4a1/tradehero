package com.tradehero.th.network.service;

import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;


public interface UserTimelineService
{
    //<editor-fold desc="Get Global Timeline">
    @GET("/timeline")
    TimelineDTO getGlobalTimeline();

    @GET("/timeline")
    TimelineDTO getGlobalTimeline(
            @Query("maxCount") int maxCount);

    @GET("/timeline/{timelineId}") TimelineItemDTO getTimelineDetail(
            @Path("timelineId") int timelineId);

    @GET("/timeline")
    TimelineDTO getGlobalTimeline(
            @Query("maxCount") int maxCount,
            @Query("maxId") Comparable maxId);

    @GET("/timeline")
    TimelineDTO getGlobalTimeline(
            @Query("maxCount") int maxCount,
            @Query("maxId") Comparable maxId,
            @Query("minId") Comparable minId);
    //</editor-fold>

    //<editor-fold desc="Get User Timeline">
    @GET("/users/{userId}/timeline")
    TimelineDTO getTimeline(
            @Path("userId") int userId);

    @GET("/users/{userId}/timeline")
    TimelineDTO getTimeline(
            @Path("userId") int userId,
            @Query("maxCount") int maxCount);

    @GET("/users/{userId}/timeline")
    TimelineDTO getTimeline(
            @Path("userId") int userId,
            @Query("maxCount") int maxCount,
            @Query("maxId") Comparable maxId);

    @GET("/users/{userId}/timeline")
    TimelineDTO getTimeline(
            @Path("userId") int userId,
            @Query("maxCount") int maxCount,
            @Query("maxId") Comparable maxId,
            @Query("minId") Comparable minId);
    //</editor-fold>

    //<editor-fold desc="Share Timeline Item">
    @POST("/users/{userId}/timeline/{timelineItemId}/share")
    Response shareTimelineItem( // Appears to return a boolean
            @Path("userId") int userId,
            @Path("timelineItemId") int timelineItemId,
            @Body TimelineItemShareRequestDTO timelineItemShareRequestDTO);
    //</editor-fold>

    //<editor-fold desc="Delete Timeline Item">
    @DELETE("/users/{userId}/timeline/{timelineItemId}")
    Response deleteTimelineItem(
            @Path("userId") int userId,
            @Path("timelineItemId") int timelineItemId);
    //</editor-fold>
}
