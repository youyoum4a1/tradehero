package com.tradehero.th.network.service;

import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;

import javax.inject.Inject;
import javax.inject.Singleton;

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

    public TimelineItemDTO getTimelineDetail(@NotNull TimelineItemDTOKey key)
    {
        return userTimelineService.getTimelineDetail(key.id);
    }

    //<editor-fold desc="Get User Timeline">
    public TimelineDTO getTimeline(
            @NotNull UserBaseKey userId, Integer maxCount, Integer maxId, Integer minId)
    {
        return userTimelineService.getTimeline(userId.key, maxCount, maxId, minId);
    }

    public @NotNull MiddleCallback<TimelineDTO> getTimelineNew(@NotNull UserBaseKey userId, Integer maxCount, Integer maxId, Integer minId,
            @Nullable Callback<TimelineDTO> callback)
    {
        BaseMiddleCallback<TimelineDTO> middleCallback = new BaseMiddleCallback<>(callback);
        userTimelineServiceAsync.getTimelineNew(userId.key, maxCount, maxId, minId, middleCallback);
        return middleCallback;
    }

    //最新动态
    public @NotNull MiddleCallback<TimelineDTO> getTimelineSquare(@NotNull UserBaseKey userId, Integer maxCount, Integer maxId, Integer minId,
            @Nullable Callback<TimelineDTO> callback)
    {
        BaseMiddleCallback<TimelineDTO> middleCallback = new BaseMiddleCallback<>(callback);
        userTimelineServiceAsync.getTimelineSquare(userId.key, maxCount, maxId, minId, middleCallback);
        return middleCallback;
    }

    //悬赏帖
    public @NotNull MiddleCallback<TimelineDTO> getTimelineReward(@NotNull UserBaseKey userId, Integer maxCount, Integer maxId, Integer minId,
            @Nullable Callback<TimelineDTO> callback)
    {
        BaseMiddleCallback<TimelineDTO> middleCallback = new BaseMiddleCallback<>(callback);
        userTimelineServiceAsync.getTimelineReward(userId.key, maxCount, maxId, minId, middleCallback);
        return middleCallback;
    }
    //股神动态
    public @NotNull MiddleCallback<TimelineDTO> getTimelineStockGodNews(@NotNull UserBaseKey userId, Integer maxCount, Integer maxId, Integer minId,
            @Nullable Callback<TimelineDTO> callback)
    {
        BaseMiddleCallback<TimelineDTO> middleCallback = new BaseMiddleCallback<>(callback);
        userTimelineServiceAsync.getTimelineStockGodNews(userId.key, maxCount, maxId, minId, middleCallback);
        return middleCallback;
    }

    //新手教学贴
    public @NotNull MiddleCallback<TimelineDTO> getTimelineLearning(@NotNull UserBaseKey userId, Integer maxCount, Integer maxId, Integer minId,
                                                                  @Nullable Callback<TimelineDTO> callback)
    {
        BaseMiddleCallback<TimelineDTO> middleCallback = new BaseMiddleCallback<>(callback);
        userTimelineServiceAsync.getTimelineLearning(userId.key, maxCount, maxId, minId, middleCallback);
        return middleCallback;
    }

    //精华帖
    public @NotNull MiddleCallback<TimelineDTO> getTimelineEssential(@NotNull UserBaseKey userId, Integer maxCount, Integer maxId, Integer minId,
                                                                    @Nullable Callback<TimelineDTO> callback)
    {
        BaseMiddleCallback<TimelineDTO> middleCallback = new BaseMiddleCallback<>(callback);
        userTimelineServiceAsync.getTimelineEssential(userId.key, maxCount, maxId, minId, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
