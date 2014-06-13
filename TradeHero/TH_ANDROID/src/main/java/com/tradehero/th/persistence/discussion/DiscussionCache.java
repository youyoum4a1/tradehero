package com.tradehero.th.persistence.discussion;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTOList;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.network.service.UserTimelineServiceWrapper;
import com.tradehero.th.persistence.SingleCacheMaxSize;
import com.tradehero.th.persistence.news.NewsItemCache;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DiscussionCache extends StraightDTOCache<DiscussionKey, AbstractDiscussionCompactDTO>
{
    private final DiscussionServiceWrapper discussionServiceWrapper;
    private final NewsItemCache newsItemCache;
    private final UserTimelineServiceWrapper timelineServiceWrapper;

    @Inject public DiscussionCache(
            @SingleCacheMaxSize IntPreference maxSize,
            NewsItemCache newsItemCache,
            UserTimelineServiceWrapper userTimelineServiceWrapper,
            DiscussionServiceWrapper discussionServiceWrapper)
    {
        super(maxSize.get());

        this.discussionServiceWrapper = discussionServiceWrapper;

        // very hacky, but server hacks it first :(
        this.newsItemCache = newsItemCache;
        this.timelineServiceWrapper = userTimelineServiceWrapper;
    }

    @Override protected AbstractDiscussionCompactDTO fetch(DiscussionKey discussionKey) throws Throwable
    {
        if (discussionKey instanceof TimelineItemDTOKey)
        {
            return timelineServiceWrapper.getTimelineDetail((TimelineItemDTOKey) discussionKey);
        }
        else if (discussionKey instanceof NewsItemDTOKey)
        {
            return newsItemCache.getOrFetch((NewsItemDTOKey) discussionKey);
        }
        return discussionServiceWrapper.getComment(discussionKey);
    }

    public DiscussionDTOList put(List<? extends AbstractDiscussionCompactDTO> discussionList)
    {
        DiscussionDTOList<? super AbstractDiscussionCompactDTO> previous = new DiscussionDTOList<>();
        for (AbstractDiscussionCompactDTO discussionDTO : discussionList)
        {
            previous.add(put(discussionDTO.getDiscussionKey(), discussionDTO));
        }
        return previous;
    }

    public DiscussionDTOList<? super AbstractDiscussionCompactDTO> get(List<DiscussionKey> discussionKeys)
    {
        if (discussionKeys == null)
        {
            return null;
        }
        DiscussionDTOList<? super AbstractDiscussionCompactDTO> dtos = new DiscussionDTOList<>();
        for (DiscussionKey discussionKey : discussionKeys)
        {
            dtos.add(get(discussionKey));
        }
        return dtos;
    }

    public DiscussionDTOList<? super AbstractDiscussionCompactDTO> getOrFetch(List<DiscussionKey> discussionKeys) throws Throwable
    {
        if (discussionKeys == null)
        {
            return null;
        }
        DiscussionDTOList<? super AbstractDiscussionCompactDTO> dtos = new DiscussionDTOList<>();
        for (DiscussionKey discussionKey : discussionKeys)
        {
            dtos.add(getOrFetch(discussionKey));
        }
        return dtos;
    }
}
