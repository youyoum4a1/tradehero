package com.tradehero.th.network.service;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import com.tradehero.th.api.timeline.TimelineSection;
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
    TimelineDTO getGlobalTimeline(
            @Query("maxCount") Integer maxCount,
            @Query("maxId") Integer maxId,
            @Query("minId") Integer minId);

    @GET("/timeline/{timelineId}") TimelineItemDTO getTimelineDetail(
            @Path("timelineId") int timelineId);
    //</editor-fold>

    //<editor-fold desc="Get User Timeline">
    @GET("/users/{userId}/{section}")
    TimelineDTO getTimeline(
            @Path("section") TimelineSection section,
            @Path("userId") int userId,
            @Query("maxCount") Integer maxCount,
            @Query("maxId") Integer maxId,
            @Query("minId") Integer minId);
    //</editor-fold>

    //<editor-fold desc="Share Timeline Item">
    @POST("/users/{userId}/timeline/{timelineItemId}/share")
    BaseResponseDTO shareTimelineItem( // Appears to return a boolean
            @Path("userId") int userId,
            @Path("timelineItemId") int timelineItemId,
            @Body TimelineItemShareRequestDTO timelineItemShareRequestDTO);
    //</editor-fold>

    //<editor-fold desc="Delete Timeline Item">
    @DELETE("/users/{userId}/timeline/{timelineItemId}")
    BaseResponseDTO deleteTimelineItem(
            @Path("userId") int userId,
            @Path("timelineItemId") int timelineItemId);
    //</editor-fold>
}
