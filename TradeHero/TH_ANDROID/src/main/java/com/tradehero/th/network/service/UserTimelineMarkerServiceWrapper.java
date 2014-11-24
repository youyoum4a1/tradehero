package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.timeline.TimelineReadDTO;
import com.tradehero.th.api.users.UserBaseKey;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton public class UserTimelineMarkerServiceWrapper
{
    @NonNull private final UserTimelineMarkerServiceRx userTimelineMarkerServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public UserTimelineMarkerServiceWrapper(
            @NonNull UserTimelineMarkerServiceRx userTimelineMarkerServiceRx)
    {
        this.userTimelineMarkerServiceRx = userTimelineMarkerServiceRx;
    }
    //</editor-fold>

    //<editor-fold desc="Post Timeline Marker">
    public Observable<TimelineReadDTO> postTimelineMarkerRx(
            @NonNull UserBaseKey userId,
            @NonNull TimelineReadDTO lastReadDTO)
    {
        return userTimelineMarkerServiceRx.postTimelineMarker(userId.key, lastReadDTO);
    }
    //</editor-fold>
}
