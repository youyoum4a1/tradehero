package com.tradehero.th.persistence.discussion;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOList;
import com.tradehero.th.api.discussion.DiscussionKeyList;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.pagination.RangedDTO;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.persistence.ListCacheMaxSize;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by thonguyen on 4/4/14.
 */
@Singleton
public class DiscussionListCache extends StraightDTOCache<DiscussionListKey, DiscussionKeyList>
{
    private final DiscussionCache discussionCache;
    private final DiscussionServiceWrapper discussionServiceWrapper;

    @Inject public DiscussionListCache(
            @ListCacheMaxSize IntPreference maxSize,
            DiscussionServiceWrapper discussionServiceWrapper,
            DiscussionCache discussionCache)
    {
        super(maxSize.get());

        this.discussionServiceWrapper = discussionServiceWrapper;
        this.discussionCache = discussionCache;
    }

    @Override protected DiscussionKeyList fetch(DiscussionListKey discussionListKey) throws Throwable
    {
        return putInternal(discussionServiceWrapper.getDiscussions(discussionListKey));
    }

    private DiscussionKeyList putInternal(RangedDTO<AbstractDiscussionDTO, DiscussionDTOList> rangedDTO)
    {
        discussionCache.put(rangedDTO.getData());
        return rangedDTO.getDataModifiable().getKeys();
    }
}
