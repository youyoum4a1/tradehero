package com.tradehero.th.network.service;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface UserTimelineServiceRx
{

    //<editor-fold desc="Get User Timeline">
    @GET("/users/{userId}/{section}")
    Observable<TimelineDTO> getTimelineRx(
            @Path("section") TimelineSection section,
            @Path("userId") int userId,
            @Query("maxCount") Integer maxCount,
            @Query("maxId") Integer maxId,
            @Query("minId") Integer minId);
    //</editor-fold>

    public enum TimelineSection
    {
        Timeline("timeline"),
        Hot("whatshot");
        private final String name;

        TimelineSection(String name)
        {
            this.name = name;
        }

        @Override public String toString()
        {
            return name;
        }
    }
}
