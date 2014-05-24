package com.tradehero.th.persistence.discussion;

import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOList;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.persistence.news.NewsItemCache;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.persistence.SingleCacheMaxSize;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class DiscussionCacheNew extends StraightDTOCacheNew<DiscussionKey, AbstractDiscussionDTO>
{
    private final DiscussionServiceWrapper discussionServiceWrapper;
    private final Lazy<NewsItemCache> newsCache;

    @Inject public DiscussionCacheNew(
            @SingleCacheMaxSize IntPreference maxSize,
            Lazy<NewsItemCache> newsCache,
            DiscussionServiceWrapper discussionServiceWrapper)
    {
        super(maxSize.get());

        this.discussionServiceWrapper = discussionServiceWrapper;

        // very hacky, but server hacks it first :(
        this.newsCache = newsCache;
    }

    @Override public AbstractDiscussionDTO fetch(DiscussionKey discussionKey) throws Throwable
    {
        if (discussionKey instanceof TimelineItemDTOKey)
        {
            // TODO
        }
        else if (discussionKey instanceof NewsItemDTOKey)
        {
            return newsCache.get().getOrFetch((NewsItemDTOKey) discussionKey);
        }
        else
        {
            return discussionServiceWrapper.getComment(discussionKey);
        }
        throw new IllegalArgumentException("Unhandled discussionKey: " + discussionKey);
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
            dtos.add(getOrFetchSync(discussionKey));
        }
        return dtos;
    }

    public static interface DiscussionListener extends DTOCache.Listener<DiscussionKey, AbstractDiscussionDTO>
    {
    }
}
