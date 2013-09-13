package com.tradehero.th.network.service;

import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/** Created with IntelliJ IDEA. User: tho Date: 9/3/13 Time: 12:47 PM Copyright (c) TradeHero */
public interface UserTimelineService
{
    @GET("/users/{userId}/timeline")
    void getTimeline(@Path("userId") int userId, @Query("maxCount") int maxCount, Callback<TimelineDTO> callback);

    @GET("/users/{userId}/timeline")
    TimelineDTO getTimeline(@Path("userId") int userId, @Query("maxId") Comparable maxId, @Query("maxCount") int maxCount);

    @GET("/users/{userId}/timeline/{timelineItemId}")
    TimelineItemDTO getTimelineItem(@Path("userId") int userId, @Path("timelineItemId") int timelineItemId);
}
