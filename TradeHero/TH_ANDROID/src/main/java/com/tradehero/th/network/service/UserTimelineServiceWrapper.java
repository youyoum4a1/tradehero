package com.tradehero.th.network.service;

import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import com.tradehero.th.api.users.UserBaseKey;
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
    public TimelineDTO getGlobalTimeline()
    {
        return userTimelineService.getGlobalTimeline();
    }

    public MiddleCallback<TimelineDTO> getGlobalTimeline(Callback<TimelineDTO> callback)
    {
        MiddleCallback<TimelineDTO> middleCallback = new MiddleCallback<>(callback);
        userTimelineServiceAsync.getGlobalTimeline(middleCallback);
        return middleCallback;
    }

    public TimelineDTO getGlobalTimeline(int maxCount)
    {
        return userTimelineService.getGlobalTimeline(maxCount);
    }

    public MiddleCallback<TimelineDTO> getGlobalTimeline(int maxCount, Callback<TimelineDTO> callback)
    {
        MiddleCallback<TimelineDTO> middleCallback = new MiddleCallback<>(callback);
        userTimelineServiceAsync.getGlobalTimeline(maxCount, middleCallback);
        return middleCallback;
    }

    public TimelineDTO getGlobalTimeline(int maxCount, Comparable maxId)
    {
        return userTimelineService.getGlobalTimeline(maxCount, maxId);
    }

    public MiddleCallback<TimelineDTO> getGlobalTimeline(int maxCount, Comparable maxId, Callback<TimelineDTO> callback)
    {
        MiddleCallback<TimelineDTO> middleCallback = new MiddleCallback<>(callback);
        userTimelineServiceAsync.getGlobalTimeline(maxCount, maxId, middleCallback);
        return middleCallback;
    }

    public TimelineDTO getGlobalTimeline(int maxCount, Comparable maxId, Comparable minId)
    {
        return userTimelineService.getGlobalTimeline(maxCount, maxId, minId);
    }

    public MiddleCallback<TimelineDTO> getGlobalTimeline(int maxCount, Comparable maxId, Comparable minId, Callback<TimelineDTO> callback)
    {
        MiddleCallback<TimelineDTO> middleCallback = new MiddleCallback<>(callback);
        userTimelineServiceAsync.getGlobalTimeline(maxCount, maxId, minId, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get User Timeline">
    public TimelineDTO getTimeline(UserBaseKey userId)
    {
        return userTimelineService.getTimeline(userId.key);
    }

    MiddleCallback<TimelineDTO> getTimeline(UserBaseKey userId, Callback<TimelineDTO> callback)
    {
        MiddleCallback<TimelineDTO> middleCallback = new MiddleCallback<>(callback);
        userTimelineServiceAsync.getTimeline(userId.key, middleCallback);
        return middleCallback;
    }

    public TimelineDTO getTimeline(UserBaseKey userId, int maxCount)
    {
        return userTimelineService.getTimeline(userId.key, maxCount);
    }

    MiddleCallback<TimelineDTO> getTimeline(UserBaseKey userId, int maxCount, Callback<TimelineDTO> callback)
    {
        MiddleCallback<TimelineDTO> middleCallback = new MiddleCallback<>(callback);
        userTimelineServiceAsync.getTimeline(userId.key, maxCount, middleCallback);
        return middleCallback;
    }

    public TimelineDTO getTimeline(UserBaseKey userId, int maxCount, Comparable maxId)
    {
        return userTimelineService.getTimeline(userId.key, maxCount, maxId);
    }

    MiddleCallback<TimelineDTO> getTimeline(UserBaseKey userId, int maxCount, Comparable maxId, Callback<TimelineDTO> callback)
    {
        MiddleCallback<TimelineDTO> middleCallback = new MiddleCallback<>(callback);
        userTimelineServiceAsync.getTimeline(userId.key, maxCount, maxId, middleCallback);
        return middleCallback;
    }

    public TimelineDTO getTimeline(UserBaseKey userId, int maxCount, Comparable maxId, Comparable minId)
    {
        return userTimelineService.getTimeline(userId.key, maxCount, maxId, minId);
    }

    MiddleCallback<TimelineDTO> getTimeline(UserBaseKey userId, int maxCount, Comparable maxId, Comparable minId, Callback<TimelineDTO> callback)
    {
        MiddleCallback<TimelineDTO> middleCallback = new MiddleCallback<>(callback);
        userTimelineServiceAsync.getTimeline(userId.key, maxCount, maxId, minId, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Share Timeline Item">
    public Response shareTimelineItem(UserBaseKey userId, int timelineItemId, TimelineItemShareRequestDTO timelineItemShareRequestDTO)
    {
        return userTimelineService.shareTimelineItem(userId.key, timelineItemId, timelineItemShareRequestDTO);
    }

    public MiddleCallback<Response> shareTimelineItem(UserBaseKey userId, int timelineItemId, TimelineItemShareRequestDTO timelineItemShareRequestDTO, Callback<Response> callback)
    {
        MiddleCallback<Response> middleCallback = new MiddleCallback<>(callback);
        userTimelineServiceAsync.shareTimelineItem(userId.key, timelineItemId, timelineItemShareRequestDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Delete Timeline Item">
    public Response deleteTimelineItem(UserBaseKey userId, int timelineItemId)
    {
        return userTimelineService.deleteTimelineItem(userId.key, timelineItemId);
    }

    public MiddleCallback<Response>  deleteTimelineItem(UserBaseKey userId, int timelineItemId, Callback<Response> callback)
    {
        MiddleCallback<Response> middleCallback = new MiddleCallback<>(callback);
        userTimelineServiceAsync.deleteTimelineItem(userId.key, timelineItemId, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
