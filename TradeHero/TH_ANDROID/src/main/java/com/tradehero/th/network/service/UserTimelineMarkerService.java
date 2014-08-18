package com.tradehero.th.network.service;

import com.tradehero.th.api.timeline.TimelineReadDTO;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

public interface UserTimelineMarkerService
{
    //<editor-fold desc="Post Timeline Marker">
    @POST("/users/{userId}/read") TimelineReadDTO postTimelineMarker(
            @Path("userId") int userId,
            @Body TimelineReadDTO lastReadDTO);
    //</editor-fold>
}
