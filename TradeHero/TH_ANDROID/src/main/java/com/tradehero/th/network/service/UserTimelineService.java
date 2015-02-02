package com.tradehero.th.network.service;

import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface UserTimelineService
{

    @GET("/timeline/{timelineId}") TimelineItemDTO getTimelineDetail(
            @Path("timelineId") int timelineId);
    //</editor-fold>

    //<editor-fold desc="Get User Timeline">
    @GET("/users/{userId}/timeline") TimelineDTO getTimeline(
            @Path("userId") int userId,
            @Query("maxCount") Integer maxCount,
            @Query("maxId") Integer maxId,
            @Query("minId") Integer minId);
    //</editor-fold>
}
