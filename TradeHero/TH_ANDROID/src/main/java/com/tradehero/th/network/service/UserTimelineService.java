package com.tradehero.th.network.service;

import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineSection;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface UserTimelineService
{
    //<editor-fold desc="Get User Timeline">
    @GET("/users/{userId}/{section}")
    TimelineDTO getTimeline(
            @Path("section") TimelineSection section,
            @Path("userId") int userId,
            @Query("maxCount") Integer maxCount,
            @Query("maxId") Integer maxId,
            @Query("minId") Integer minId);
    //</editor-fold>
}
