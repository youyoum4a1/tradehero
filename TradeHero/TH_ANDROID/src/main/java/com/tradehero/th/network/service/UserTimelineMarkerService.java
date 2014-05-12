package com.tradehero.th.network.service;

import com.tradehero.th.api.timeline.TimelineReadDTO;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;


public interface UserTimelineMarkerService
{
    //<editor-fold desc="Post Timeline Marker">
    @POST("/users/{userId}/read") TimelineReadDTO postTimelineMarker(
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
