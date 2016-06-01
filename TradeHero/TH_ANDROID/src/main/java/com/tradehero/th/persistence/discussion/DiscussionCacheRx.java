package com.ayondo.academy.persistence.discussion;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.discussion.AbstractDiscussionCompactDTO;
import com.ayondo.academy.api.discussion.DiscussionDTO;
import com.ayondo.academy.api.discussion.key.DiscussionKey;
import com.ayondo.academy.api.news.NewsItemDTO;
import com.ayondo.academy.api.news.key.NewsItemDTOKey;
import com.ayondo.academy.api.timeline.TimelineItemDTO;
import com.ayondo.academy.api.timeline.key.TimelineItemDTOKey;
import com.ayondo.academy.network.service.DiscussionServiceWrapper;
import com.ayondo.academy.network.service.NewsServiceWrapper;
import com.ayondo.academy.network.service.UserTimelineServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.functions.Func1;

@Singleton @UserCache
public class DiscussionCacheRx extends BaseFetchDTOCacheRx<DiscussionKey, AbstractDiscussionCompactDTO>
{
    private static final int DEFAULT_VALUE_SIZE = 100;

    @NonNull private final DiscussionServiceWrapper discussionServiceWrapper;
    @NonNull private final NewsServiceWrapper newsServiceWrapper;
    @NonNull private final UserTimelineServiceWrapper timelineServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public DiscussionCacheRx(
            @NonNull NewsServiceWrapper newsServiceWrapper,
            @NonNull UserTimelineServiceWrapper userTimelineServiceWrapper,
            @NonNull DiscussionServiceWrapper discussionServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_VALUE_SIZE, dtoCacheUtil);

        this.discussionServiceWrapper = discussionServiceWrapper;
        this.newsServiceWrapper = newsServiceWrapper;
        this.timelineServiceWrapper = userTimelineServiceWrapper;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<AbstractDiscussionCompactDTO> fetch(@NonNull DiscussionKey discussionKey)
    {
        if (discussionKey instanceof TimelineItemDTOKey)
        {
            return timelineServiceWrapper.getTimelineDetailRx((TimelineItemDTOKey) discussionKey)
                    .map(new Func1<TimelineItemDTO, AbstractDiscussionCompactDTO>()
                    {
                        @Override public AbstractDiscussionCompactDTO call(TimelineItemDTO timelineItem)
                        {
                            return timelineItem;
                        }
                    });
        }
        else if (discussionKey instanceof NewsItemDTOKey)
        {
            return newsServiceWrapper.getSecurityNewsDetailRx(discussionKey)
                    .map(new Func1<NewsItemDTO, AbstractDiscussionCompactDTO>()
                    {
                        @Override public AbstractDiscussionCompactDTO call(NewsItemDTO news)
                        {
                            return news;
                        }
                    });
        }
        return discussionServiceWrapper.getCommentRx(discussionKey)
                .map(new Func1<DiscussionDTO, AbstractDiscussionCompactDTO>()
                {
                    @Override public AbstractDiscussionCompactDTO call(DiscussionDTO comment)
                    {
                        return comment;
                    }
                });
    }

    public void onNext(@NonNull List<? extends AbstractDiscussionCompactDTO> discussionList)
    {
        for (AbstractDiscussionCompactDTO discussionDTO : discussionList)
        {
            onNext(discussionDTO.getDiscussionKey(), discussionDTO);
        }
    }
}
