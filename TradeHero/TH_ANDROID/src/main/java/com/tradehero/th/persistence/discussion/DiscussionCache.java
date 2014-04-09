package com.tradehero.th.persistence.discussion;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOList;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.network.service.DiscussionService;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.persistence.SingleCacheMaxSize;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by thonguyen on 4/4/14.
 */
@Singleton
public class DiscussionCache extends StraightDTOCache<DiscussionKey, DiscussionDTO>
{
    private final DiscussionServiceWrapper discussionServiceWrapper;

    @Inject public DiscussionCache(
            @SingleCacheMaxSize IntPreference maxSize,
            DiscussionServiceWrapper discussionServiceWrapper)
    {
        super(maxSize.get());

        this.discussionServiceWrapper = discussionServiceWrapper;
    }

    @Override protected DiscussionDTO fetch(DiscussionKey key) throws Throwable
    {
        return discussionServiceWrapper.getComment(key);
    }

    public DiscussionDTOList put(List<DiscussionDTO> discussionList)
    {
        DiscussionDTOList previous = new DiscussionDTOList();
        for (DiscussionDTO discussionDTO : discussionList)
        {
            previous.add(put(discussionDTO.getDiscussionKey(), discussionDTO));
        }
        return previous;
    }
}
