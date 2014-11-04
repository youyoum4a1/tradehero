package com.tradehero.th.network.service;

import com.tradehero.th.api.timeline.TimelineReadDTO;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

interface UserTimelineMarkerServiceRx
{
    //<editor-fold desc="Post Timeline Marker">
    @POST("/users/{userId}/read")
    Observable<TimelineReadDTO> postTimelineMarker(
            @Path("userId") int userId,
            @Body TimelineReadDTO lastReadDTO);
    //</editor-fold>
}
