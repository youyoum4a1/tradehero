package com.tradehero.th.network.service;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.pagination.RangeDTO;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import com.tradehero.th.api.timeline.TimelineSection;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.timeline.TimelineDTOProcessor;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import rx.Observable;

@Singleton public class UserTimelineServiceWrapper
{
    @NotNull private final UserTimelineService userTimelineService;
    @NotNull private final UserTimelineServiceAsync userTimelineServiceAsync;
    @NotNull private final UserTimelineServiceRx userTimelineServiceRx;
    @NotNull private final Provider<TimelineDTOProcessor> timelineProcessorProvider;

    //<editor-fold desc="Constructors">
    @Inject public UserTimelineServiceWrapper(
            @NotNull UserTimelineService userTimelineService,
            @NotNull UserTimelineServiceAsync userTimelineServiceAsync,
            @NotNull UserTimelineServiceRx userTimelineServiceRx,
            @NotNull Provider<TimelineDTOProcessor> timelineProcessorProvider)
    {
        super();
        this.userTimelineService = userTimelineService;
        this.userTimelineServiceAsync = userTimelineServiceAsync;
        this.userTimelineServiceRx = userTimelineServiceRx;
        this.timelineProcessorProvider = timelineProcessorProvider;
    }
    //</editor-fold>

    //<editor-fold desc="Get Global Timeline">
    // TODO create a proper key that contains the values max / min
    @NotNull protected TimelineDTO getGlobalTimeline(Integer maxCount, Integer maxId, Integer minId)
    {
        return userTimelineService.getGlobalTimeline(maxCount, maxId, minId);
    }

    @NotNull protected MiddleCallback<TimelineDTO> getGlobalTimeline(Integer maxCount, Integer maxId, Integer minId, Callback<TimelineDTO> callback)
    {
        MiddleCallback<TimelineDTO> middleCallback = new BaseMiddleCallback<>(callback);
        userTimelineServiceAsync.getGlobalTimeline(maxCount, maxId, minId, middleCallback);
        return middleCallback;
    }

    @NotNull public TimelineItemDTO getTimelineDetail(@NotNull TimelineItemDTOKey key)
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
    @NotNull public TimelineDTO getDefaultTimeline(@NotNull UserBaseKey userId, Integer maxCount, Integer maxId, Integer minId)
    {
        // Make a key that contains all info.
        return userTimelineService.getTimeline(TimelineSection.Timeline, userId.key, maxCount, maxId, minId);
    }

    public TimelineDTO getTimelineBySection(TimelineSection section,
            @NotNull UserBaseKey userId, Integer maxCount, Integer maxId, Integer minId)
    {
        // Make a key that contains all info.
        return userTimelineService.getTimeline(section, userId.key, maxCount, maxId, minId);
    }

    public Observable<TimelineDTO> getTimelineBySectionRx(TimelineSection section,
            @NotNull UserBaseKey userId, Integer maxCount, Integer maxId, Integer minId)
    {
        // Make a key that contains all info.
        return userTimelineServiceRx.getTimelineRx(section, userId.key, maxCount, maxId, minId)
                .doOnNext(timelineProcessorProvider.get());
    }

    public Observable<TimelineDTO> getTimelineBySectionRx(TimelineSection section, @NotNull UserBaseKey userBaseKey, RangeDTO rangeDTO)
    {
        return getTimelineBySectionRx(section, userBaseKey, rangeDTO.maxCount, rangeDTO.maxId, rangeDTO.minId);
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
    @NotNull public BaseResponseDTO shareTimelineItem(
            @NotNull UserBaseKey userId,
            @NotNull TimelineItemDTOKey timelineItemId,
            @NotNull TimelineItemShareRequestDTO timelineItemShareRequestDTO)
    {
        return userTimelineService.shareTimelineItem(userId.key, timelineItemId.id, timelineItemShareRequestDTO);
    }

    @NotNull public MiddleCallback<BaseResponseDTO> shareTimelineItem(
            @NotNull UserBaseKey userId,
            @NotNull TimelineItemDTOKey timelineItemId,
            @NotNull TimelineItemShareRequestDTO timelineItemShareRequestDTO,
            @Nullable Callback<BaseResponseDTO> callback)
    {
        MiddleCallback<BaseResponseDTO> middleCallback = new BaseMiddleCallback<>(callback);
        userTimelineServiceAsync.shareTimelineItem(userId.key, timelineItemId.id, timelineItemShareRequestDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Delete Timeline Item">
    @NotNull public BaseResponseDTO deleteTimelineItem(
            @NotNull UserBaseKey userId,
            @NotNull TimelineItemDTOKey timelineItemId)
    {
        return userTimelineService.deleteTimelineItem(userId.key, timelineItemId.id);
    }

    @NotNull public MiddleCallback<BaseResponseDTO> deleteTimelineItem(
            @NotNull UserBaseKey userId,
            @NotNull TimelineItemDTOKey timelineItemId,
            @Nullable Callback<BaseResponseDTO> callback)
    {
        MiddleCallback<BaseResponseDTO> middleCallback = new BaseMiddleCallback<>(callback);
        userTimelineServiceAsync.deleteTimelineItem(userId.key, timelineItemId.id, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
