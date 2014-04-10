package com.tradehero.th.persistence.discussion;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOList;
import com.tradehero.th.api.discussion.key.DiscussionKey;
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

    @Override protected AbstractDiscussionDTO fetch(DiscussionKey key) throws Throwable
    {
        return discussionServiceWrapper.getComment(key);
    }

    public DiscussionDTOList put(List<AbstractDiscussionDTO> discussionList)
    {
        DiscussionDTOList previous = new DiscussionDTOList();
        for (AbstractDiscussionDTO discussionDTO : discussionList)
        {
            previous.add(put(discussionDTO.getDiscussionKey(), discussionDTO));
        }
        return previous;
    }

    public DiscussionDTOList get(List<DiscussionKey> discussionKeys)
    {
        if (discussionKeys == null)
        {
            return null;
        }
        DiscussionDTOList dtos = new DiscussionDTOList();
        for (DiscussionKey discussionKey : discussionKeys)
        {
            dtos.add(get(discussionKey));
        }
        return dtos;
    }

    public DiscussionDTOList getOrFetch(List<DiscussionKey> discussionKeys) throws Throwable
    {
        if (discussionKeys == null)
        {
            return null;
        }
        DiscussionDTOList dtos = new DiscussionDTOList();
        for (DiscussionKey discussionKey : discussionKeys)
        {
            dtos.add(getOrFetch(discussionKey));
        }
        return dtos;
    }
}
