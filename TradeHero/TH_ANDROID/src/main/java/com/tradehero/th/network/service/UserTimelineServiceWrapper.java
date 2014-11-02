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
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton public class UserTimelineServiceWrapper
{
    @NotNull private final UserTimelineService userTimelineService;
    @NotNull private final UserTimelineServiceRx userTimelineServiceRx;
    @NotNull private final Provider<TimelineDTOProcessor> timelineProcessorProvider;

    //<editor-fold desc="Constructors">
    @Inject public UserTimelineServiceWrapper(
            @NotNull UserTimelineService userTimelineService,
            @NotNull UserTimelineServiceRx userTimelineServiceRx,
            @NotNull Provider<TimelineDTOProcessor> timelineProcessorProvider)
    {
        super();
        this.userTimelineService = userTimelineService;
        this.userTimelineServiceRx = userTimelineServiceRx;
        this.timelineProcessorProvider = timelineProcessorProvider;
    }
    //</editor-fold>

    //<editor-fold desc="Get Global Timeline">
    // TODO create a proper key that contains the values max / min
    @NotNull protected Observable<TimelineDTO> getGlobalTimelineRx(Integer maxCount, Integer maxId, Integer minId)
    {
        return userTimelineServiceRx.getGlobalTimeline(maxCount, maxId, minId);
    }

    @NotNull public TimelineItemDTO getTimelineDetail(@NotNull TimelineItemDTOKey key)
    {
        return userTimelineService.getTimelineDetail(key.id);
    }

    @NotNull public Observable<TimelineItemDTO> getTimelineDetailRx(@NotNull TimelineItemDTOKey key)
    {
        return userTimelineServiceRx.getTimelineDetail(key.id);
    }
    //</editor-fold>

    //<editor-fold desc="Get User Timeline">
    @NotNull public Observable<TimelineDTO> getDefaultTimelineRx(@NotNull UserBaseKey userId, Integer maxCount, Integer maxId, Integer minId)
    {
        // Make a key that contains all info.
        return userTimelineServiceRx.getTimeline(TimelineSection.Timeline, userId.key, maxCount, maxId, minId);
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
        return userTimelineServiceRx.getTimeline(section, userId.key, maxCount, maxId, minId)
                .doOnNext(timelineProcessorProvider.get());
    }

    public Observable<TimelineDTO> getTimelineBySectionRx(TimelineSection section, @NotNull UserBaseKey userBaseKey, RangeDTO rangeDTO)
    {
        return getTimelineBySectionRx(section, userBaseKey, rangeDTO.maxCount, rangeDTO.maxId, rangeDTO.minId);
    }
    //</editor-fold>

    //<editor-fold desc="Share Timeline Item">
    @NotNull public Observable<BaseResponseDTO> shareTimelineItem(
            @NotNull UserBaseKey userId,
            @NotNull TimelineItemDTOKey timelineItemId,
            @NotNull TimelineItemShareRequestDTO timelineItemShareRequestDTO)
    {
        return userTimelineServiceRx.shareTimelineItem(userId.key, timelineItemId.id, timelineItemShareRequestDTO);
    }
    //</editor-fold>

    //<editor-fold desc="Delete Timeline Item">
    @NotNull public Observable<BaseResponseDTO> deleteTimelineItem(
            @NotNull UserBaseKey userId,
            @NotNull TimelineItemDTOKey timelineItemId)
    {
        return userTimelineServiceRx.deleteTimelineItem(userId.key, timelineItemId.id);
    }
    //</editor-fold>
}
