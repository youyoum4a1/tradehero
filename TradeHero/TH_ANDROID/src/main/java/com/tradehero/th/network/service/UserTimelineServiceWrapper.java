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
import retrofit.Callback;
import retrofit.client.Response;

@Singleton public class UserTimelineServiceWrapper
{
    private final UserTimelineService userTimelineService;
    private final UserTimelineServiceAsync userTimelineServiceAsync;

    @Inject public UserTimelineServiceWrapper(
            UserTimelineService userTimelineService,
            UserTimelineServiceAsync userTimelineServiceAsync)
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

    protected BaseMiddleCallback<TimelineDTO> getGlobalTimeline(Integer maxCount, Integer maxId, Integer minId, Callback<TimelineDTO> callback)
    {
        BaseMiddleCallback<TimelineDTO> middleCallback = new BaseMiddleCallback<>(callback);
        userTimelineServiceAsync.getGlobalTimeline(maxCount, maxId, minId, middleCallback);
        return middleCallback;
    }

    public TimelineItemDTO getTimelineDetail(TimelineItemDTOKey key)
    {
        return userTimelineService.getTimelineDetail(key.id);
    }

    public MiddleCallback<TimelineItemDTO> getTimelineDetail(TimelineItemDTOKey key, Callback<TimelineItemDTO> callback)
    {
        MiddleCallback<TimelineItemDTO> middleCallback = new BaseMiddleCallback<>(callback);
        userTimelineServiceAsync.getTimelineDetail(key.id, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get User Timeline">
    public TimelineDTO getTimeline(UserBaseKey userId, Integer maxCount, Integer maxId, Integer minId)
    {
        return userTimelineService.getTimeline(userId.key, maxCount, maxId, minId);
    }

    BaseMiddleCallback<TimelineDTO> getTimeline(UserBaseKey userId, Integer maxCount, Integer maxId, Integer minId, Callback<TimelineDTO> callback)
    {
        BaseMiddleCallback<TimelineDTO> middleCallback = new BaseMiddleCallback<>(callback);
        userTimelineServiceAsync.getTimeline(userId.key, maxCount, maxId, minId, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Share Timeline Item">
    public Response shareTimelineItem(UserBaseKey userId, int timelineItemId, TimelineItemShareRequestDTO timelineItemShareRequestDTO)
    {
        return userTimelineService.shareTimelineItem(userId.key, timelineItemId, timelineItemShareRequestDTO);
    }

    public BaseMiddleCallback<Response> shareTimelineItem(UserBaseKey userId, int timelineItemId, TimelineItemShareRequestDTO timelineItemShareRequestDTO, Callback<Response> callback)
    {
        BaseMiddleCallback<Response> middleCallback = new BaseMiddleCallback<>(callback);
        userTimelineServiceAsync.shareTimelineItem(userId.key, timelineItemId, timelineItemShareRequestDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Delete Timeline Item">
    public Response deleteTimelineItem(UserBaseKey userId, int timelineItemId)
    {
        return userTimelineService.deleteTimelineItem(userId.key, timelineItemId);
    }

    public BaseMiddleCallback<Response> deleteTimelineItem(UserBaseKey userId, int timelineItemId, Callback<Response> callback)
    {
        BaseMiddleCallback<Response> middleCallback = new BaseMiddleCallback<>(callback);
        userTimelineServiceAsync.deleteTimelineItem(userId.key, timelineItemId, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
