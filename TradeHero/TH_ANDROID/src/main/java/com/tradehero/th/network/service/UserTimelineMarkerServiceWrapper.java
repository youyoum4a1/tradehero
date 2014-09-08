package com.tradehero.th.network.service;

import com.tradehero.th.api.timeline.TimelineReadDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;

@Singleton public class UserTimelineMarkerServiceWrapper
{
    @NotNull private final UserTimelineMarkerService userTimelineMarkerService;
    @NotNull private final UserTimelineMarkerServiceAsync userTimelineMarkerServiceAsync;

    //<editor-fold desc="Constructors">
    @Inject public UserTimelineMarkerServiceWrapper(
            @NotNull UserTimelineMarkerService userTimelineMarkerService,
            @NotNull UserTimelineMarkerServiceAsync userTimelineMarkerServiceAsync)
    {
        this.userTimelineMarkerService = userTimelineMarkerService;
        this.userTimelineMarkerServiceAsync = userTimelineMarkerServiceAsync;
    }
    //</editor-fold>

    //<editor-fold desc="Post Timeline Marker">
    public TimelineReadDTO postTimelineMarker(
            @NotNull UserBaseKey userId,
            @NotNull TimelineReadDTO lastReadDTO)
    {
        return userTimelineMarkerService.postTimelineMarker(userId.key, lastReadDTO);
    }

    public MiddleCallback<TimelineReadDTO> postTimelineMarker(
            @NotNull UserBaseKey userId,
            @NotNull TimelineReadDTO lastReadDTO,
            @Nullable Callback<TimelineReadDTO> callback)
    {
        MiddleCallback<TimelineReadDTO> middleCallback = new BaseMiddleCallback<>(callback);
        userTimelineMarkerServiceAsync.postTimelineMarker(userId.key, lastReadDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
