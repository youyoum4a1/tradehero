package com.tradehero.th.network.service;

import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.client.Response;

@Singleton public class UserTimelineServiceWrapper
{
    @NotNull private final UserTimelineService userTimelineService;
    @NotNull private final UserTimelineServiceAsync userTimelineServiceAsync;

    @Inject public UserTimelineServiceWrapper(
            @NotNull UserTimelineService userTimelineService,
            @NotNull UserTimelineServiceAsync userTimelineServiceAsync)
    {
        super();
        this.userTimelineService = userTimelineService;
        this.userTimelineServiceAsync = userTimelineServiceAsync;
    }

    //<editor-fold desc="Get Global Timeline">
    // TODO create a proper key that contains the values max / min
    protected TimelineDTO getGlobalTimeline(Integer maxCount, Integer maxId, Integer minId)
    {
        return userTimelineService.getGlobalTimeline(maxCount, maxId, minId);
    }

    @NotNull protected MiddleCallback<TimelineDTO> getGlobalTimeline(Integer maxCount, Integer maxId, Integer minId, Callback<TimelineDTO> callback)
    {
        MiddleCallback<TimelineDTO> middleCallback = new BaseMiddleCallback<>(callback);
        userTimelineServiceAsync.getGlobalTimeline(maxCount, maxId, minId, middleCallback);
        return middleCallback;
    }

    public TimelineItemDTO getTimelineDetail(@NotNull TimelineItemDTOKey key)
    {
        return userTimelineService.getTimelineDetail(key.id);
    }

    @NotNull public MiddleCallback<TimelineItemDTO> getTimelineDetail(
            @NotNull TimelineItemDTOKey key,
            @Nullable Callback<TimelineItemDTO> callback)
    {
        MiddleCallback<TimelineItemDTO> middleCallback = new BaseMiddleCallback<>(callback);
        userTimelineServiceAsync.getTimelineDetail(key.id, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get User Timeline">
    public TimelineDTO getTimeline(
            @NotNull UserBaseKey userId, Integer maxCount, Integer maxId, Integer minId)
    {
        return userTimelineService.getTimeline(userId.key, maxCount, maxId, minId);
    }

    @NotNull MiddleCallback<TimelineDTO> getTimeline(@NotNull UserBaseKey userId, Integer maxCount, Integer maxId, Integer minId,
            @Nullable Callback<TimelineDTO> callback)
    {
        BaseMiddleCallback<TimelineDTO> middleCallback = new BaseMiddleCallback<>(callback);
        userTimelineServiceAsync.getTimeline(userId.key, maxCount, maxId, minId, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Share Timeline Item">
    public Response shareTimelineItem(@NotNull UserBaseKey userId, int timelineItemId, TimelineItemShareRequestDTO timelineItemShareRequestDTO)
    {
        return userTimelineService.shareTimelineItem(userId.key, timelineItemId, timelineItemShareRequestDTO);
    }

    @NotNull public MiddleCallback<Response> shareTimelineItem(@NotNull UserBaseKey userId, int timelineItemId, TimelineItemShareRequestDTO timelineItemShareRequestDTO, @Nullable Callback<Response> callback)
    {
        MiddleCallback<Response> middleCallback = new BaseMiddleCallback<>(callback);
        userTimelineServiceAsync.shareTimelineItem(userId.key, timelineItemId, timelineItemShareRequestDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Delete Timeline Item">
    public Response deleteTimelineItem(@NotNull UserBaseKey userId, int timelineItemId)
    {
        return userTimelineService.deleteTimelineItem(userId.key, timelineItemId);
    }

    @NotNull public MiddleCallback<Response> deleteTimelineItem(@NotNull UserBaseKey userId, int timelineItemId, @Nullable Callback<Response> callback)
    {
        MiddleCallback<Response> middleCallback = new BaseMiddleCallback<>(callback);
        userTimelineServiceAsync.deleteTimelineItem(userId.key, timelineItemId, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
