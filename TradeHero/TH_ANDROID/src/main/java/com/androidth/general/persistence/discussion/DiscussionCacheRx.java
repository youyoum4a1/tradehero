package com.androidth.general.persistence.discussion;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.discussion.AbstractDiscussionCompactDTO;
import com.androidth.general.api.discussion.DiscussionDTO;
import com.androidth.general.api.discussion.key.DiscussionKey;
import com.androidth.general.api.news.NewsItemDTO;
import com.androidth.general.api.news.key.NewsItemDTOKey;
import com.androidth.general.api.timeline.TimelineItemDTO;
import com.androidth.general.api.timeline.key.TimelineItemDTOKey;
import com.androidth.general.network.service.DiscussionServiceWrapper;
import com.androidth.general.network.service.NewsServiceWrapper;
import com.androidth.general.network.service.UserTimelineServiceWrapper;
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
