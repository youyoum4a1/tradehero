package com.tradehero.th.network.service;

import com.tradehero.th.api.timeline.TimelineReadDTO;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

interface UserTimelineMarkerServiceAsync
{
    //<editor-fold desc="Post Timeline Marker">
    @POST("/users/{userId}/read")
    void postTimelineMarker(
            @Path("userId") int userId,
            @Body TimelineReadDTO lastReadDTO,
            Callback<TimelineReadDTO> callback);
    //</editor-fold>
}
