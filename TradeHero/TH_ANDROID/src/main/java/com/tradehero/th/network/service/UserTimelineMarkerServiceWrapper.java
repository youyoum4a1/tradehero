package com.tradehero.th.network.service;

import com.tradehero.th.api.timeline.TimelineReadDTO;
import com.tradehero.th.api.users.UserBaseKey;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton public class UserTimelineMarkerServiceWrapper
{
    @NotNull private final UserTimelineMarkerServiceRx userTimelineMarkerServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public UserTimelineMarkerServiceWrapper(
            @NotNull UserTimelineMarkerServiceRx userTimelineMarkerServiceRx)
    {
        this.userTimelineMarkerServiceRx = userTimelineMarkerServiceRx;
    }
    //</editor-fold>

    //<editor-fold desc="Post Timeline Marker">
    public Observable<TimelineReadDTO> postTimelineMarkerRx(
            @NotNull UserBaseKey userId,
            @NotNull TimelineReadDTO lastReadDTO)
    {
        return userTimelineMarkerServiceRx.postTimelineMarker(userId.key, lastReadDTO);
    }
    //</editor-fold>
}
