package com.androidth.general.network.service;

import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.timeline.TimelineDTO;
import com.androidth.general.api.timeline.TimelineItemDTO;
import com.androidth.general.api.timeline.TimelineItemShareRequestDTO;
import com.androidth.general.api.timeline.TimelineSection;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface UserTimelineServiceRx
{
    //<editor-fold desc="Get Global Timeline">
    @GET("/timeline")
    Observable<TimelineDTO> getGlobalTimeline(
            @Query("maxCount") Integer maxCount,
            @Query("maxId") Integer maxId,
            @Query("minId") Integer minId);

    @GET("/timeline/{timelineId}")
    Observable<TimelineItemDTO> getTimelineDetail(
            @Path("timelineId") int timelineId);
    //</editor-fold>

    //<editor-fold desc="Get User Timeline">
    @GET("/users/{userId}/{section}")
    Observable<TimelineDTO> getTimeline(
            @Path("section") TimelineSection section,
            @Path("userId") int userId,
            @Query("maxCount") Integer maxCount,
            @Query("maxId") Integer maxId,
            @Query("minId") Integer minId);
    //</editor-fold>

    //<editor-fold desc="Share Timeline Item">
    @POST("/users/{userId}/timeline/{timelineItemId}/share")
    Observable<BaseResponseDTO> shareTimelineItem( // Appears to return a boolean
            @Path("userId") int userId,
            @Path("timelineItemId") int timelineItemId,
            @Body TimelineItemShareRequestDTO timelineItemShareRequestDTO);
    //</editor-fold>

    //<editor-fold desc="Delete Timeline Item">
    @DELETE("/users/{userId}/timeline/{timelineItemId}")
    Observable<BaseResponseDTO> deleteTimelineItem(
            @Path("userId") int userId,
            @Path("timelineItemId") int timelineItemId);
    //</editor-fold>
}
