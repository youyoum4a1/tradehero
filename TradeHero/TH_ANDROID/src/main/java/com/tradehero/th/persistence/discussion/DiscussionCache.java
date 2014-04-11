package com.tradehero.th.persistence.discussion;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOList;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.news.NewsItemDTOKey;
import com.tradehero.th.api.timeline.TimelineItemDTOKey;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.persistence.SingleCacheMaxSize;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by thonguyen on 4/4/14.
 */
@Singleton
public class DiscussionCache extends StraightDTOCache<DiscussionKey, AbstractDiscussionDTO>
{
    private final DiscussionServiceWrapper discussionServiceWrapper;

    @Inject public DiscussionCache(
            @SingleCacheMaxSize IntPreference maxSize,
            DiscussionServiceWrapper discussionServiceWrapper)
    {
        super(maxSize.get());

        this.discussionServiceWrapper = discussionServiceWrapper;
    }

    @Override protected AbstractDiscussionDTO fetch(DiscussionKey discussionKey) throws Throwable
    {
        if (discussionKey instanceof TimelineItemDTOKey)
        {
            // TODO
        }
        else if (discussionKey instanceof NewsItemDTOKey)
        {
            // TODO
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
            dtos.add(getOrFetch(discussionKey));
        }
        return dtos;
    }
}
