package com.tradehero.th.persistence.discussion;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.network.service.NewsServiceWrapper;
import com.tradehero.th.network.service.UserTimelineServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton @UserCache
public class DiscussionCacheRx extends BaseFetchDTOCacheRx<DiscussionKey, AbstractDiscussionCompactDTO>
{
    private static final int DEFAULT_VALUE_SIZE = 100;
    private static final int DEFAULT_SUBJECT_SIZE = 10;

    @NotNull private final DiscussionServiceWrapper discussionServiceWrapper;
    @NotNull private final NewsServiceWrapper newsServiceWrapper;
    @NotNull private final UserTimelineServiceWrapper timelineServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public DiscussionCacheRx(
            @NotNull NewsServiceWrapper newsServiceWrapper,
            @NotNull UserTimelineServiceWrapper userTimelineServiceWrapper,
            @NotNull DiscussionServiceWrapper discussionServiceWrapper,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_VALUE_SIZE, DEFAULT_SUBJECT_SIZE, DEFAULT_SUBJECT_SIZE, dtoCacheUtil);

        this.discussionServiceWrapper = discussionServiceWrapper;
        this.newsServiceWrapper = newsServiceWrapper;
        this.timelineServiceWrapper = userTimelineServiceWrapper;
    }
    //</editor-fold>

    @Override @NotNull protected Observable<AbstractDiscussionCompactDTO> fetch(@NotNull DiscussionKey discussionKey)
    {
        if (discussionKey instanceof TimelineItemDTOKey)
        {
            return timelineServiceWrapper.getTimelineDetailRx((TimelineItemDTOKey) discussionKey)
                    .map(timelineItem -> (AbstractDiscussionCompactDTO) timelineItem);
        }
        else if (discussionKey instanceof NewsItemDTOKey)
        {
            return newsServiceWrapper.getSecurityNewsDetailRx(discussionKey)
                    .map(news -> (AbstractDiscussionCompactDTO) news);
        }
        return discussionServiceWrapper.getCommentRx(discussionKey)
                .map(comment -> (AbstractDiscussionCompactDTO) comment);
    }

    public void onNext(@NotNull List<? extends AbstractDiscussionCompactDTO> discussionList)
    {
        for (AbstractDiscussionCompactDTO discussionDTO : discussionList)
        {
            onNext(discussionDTO.getDiscussionKey(), discussionDTO);
        }
    }
}
