package com.tradehero.th.persistence.discussion;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOList;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.news.NewsCache;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.network.service.UserTimelineServiceWrapper;
import com.tradehero.th.persistence.SingleCacheMaxSize;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DiscussionCache extends StraightDTOCache<DiscussionKey, AbstractDiscussionDTO>
{
    private final DiscussionServiceWrapper discussionServiceWrapper;
    private final NewsCache newsCache;
    private final UserTimelineServiceWrapper timelineServiceWrapper;

    @Inject public DiscussionCache(
            @SingleCacheMaxSize IntPreference maxSize,
            NewsCache newsCache,
            UserTimelineServiceWrapper userTimelineServiceWrapper,
            DiscussionServiceWrapper discussionServiceWrapper)
    {
        super(maxSize.get());

        this.discussionServiceWrapper = discussionServiceWrapper;

        // very hacky, but server hacks it first :(
        this.newsCache = newsCache;
        this.timelineServiceWrapper = userTimelineServiceWrapper;
    }

    @Override protected AbstractDiscussionDTO fetch(DiscussionKey discussionKey) throws Throwable
    {
        if (discussionKey instanceof TimelineItemDTOKey)
        {
            return timelineServiceWrapper.getTimelineDetail((TimelineItemDTOKey) discussionKey);
        }
        else if (discussionKey instanceof NewsItemDTOKey)
        {
            return newsCache.getOrFetch((NewsItemDTOKey) discussionKey);
        }
        else
        {
            return discussionServiceWrapper.getComment(discussionKey);
        }
        //throw new IllegalArgumentException("Unhandled discussionKey: " + discussionKey);
    }

    public DiscussionDTOList put(List<? extends AbstractDiscussionDTO> discussionList)
    {
        DiscussionDTOList<? super AbstractDiscussionDTO> previous = new DiscussionDTOList<>();
        for (AbstractDiscussionDTO discussionDTO : discussionList)
        {
            previous.add(put(discussionDTO.getDiscussionKey(), discussionDTO));
        }
        return previous;
    }

    public DiscussionDTOList<? super AbstractDiscussionDTO> get(List<DiscussionKey> discussionKeys)
    {
        if (discussionKeys == null)
        {
            return null;
        }
        DiscussionDTOList<? super AbstractDiscussionDTO> dtos = new DiscussionDTOList<>();
        for (DiscussionKey discussionKey : discussionKeys)
        {
            dtos.add(get(discussionKey));
        }
        return dtos;
    }

    public DiscussionDTOList<? super AbstractDiscussionDTO> getOrFetch(List<DiscussionKey> discussionKeys) throws Throwable
    {
        if (discussionKeys == null)
        {
            return null;
        }
        DiscussionDTOList<? super AbstractDiscussionDTO> dtos = new DiscussionDTOList<>();
        for (DiscussionKey discussionKey : discussionKeys)
        {
            dtos.add(getOrFetch(discussionKey));
        }
        return dtos;
    }
}
