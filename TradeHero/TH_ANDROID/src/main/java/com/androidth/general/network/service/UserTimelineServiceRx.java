package com.androidth.general.network.service;

import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.timeline.TimelineDTO;
import com.androidth.general.api.timeline.TimelineItemDTO;
import com.androidth.general.api.timeline.TimelineItemShareRequestDTO;
import com.androidth.general.api.timeline.TimelineSection;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface UserTimelineServiceRx
{
    //<editor-fold desc="Get Global Timeline">
    @GET("api/timeline")
    Observable<TimelineDTO> getGlobalTimeline(
            @Query("maxCount") Integer maxCount,
            @Query("maxId") Integer maxId,
            @Query("minId") Integer minId);

    @GET("api/timeline/{timelineId}")
    Observable<TimelineItemDTO> getTimelineDetail(
            @Path("timelineId") int timelineId);
    //</editor-fold>

    //<editor-fold desc="Get User Timeline">
    @GET("api/users/{userId}/{section}")
    Observable<TimelineDTO> getTimeline(
            @Path("section") TimelineSection section,
            @Path("userId") int userId,
            @Query("maxCount") Integer maxCount,
            @Query("maxId") Integer maxId,
            @Query("minId") Integer minId);
    //</editor-fold>

    //<editor-fold desc="Share Timeline Item">
    @POST("api/users/{userId}/timeline/{timelineItemId}/share")
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
