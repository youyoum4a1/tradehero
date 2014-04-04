package com.tradehero.th.persistence.discussion;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.discussion.DiscussionKeyList;
import com.tradehero.th.api.discussion.DiscussionListKey;
import com.tradehero.th.network.service.DiscussionService;
import com.tradehero.th.persistence.ListCacheMaxSize;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by thonguyen on 4/4/14.
 */
@Singleton
public class DiscussionListCache extends StraightDTOCache<DiscussionListKey, DiscussionKeyList>
{
    private final Lazy<DiscussionCache> discussionCache;
    private final Lazy<DiscussionService> discussionService;

    @Inject public DiscussionListCache(
            @ListCacheMaxSize IntPreference maxSize,
            Lazy<DiscussionService> discussionService,
            Lazy<DiscussionCache> discussionCache
    )
    {
        super(maxSize.get());

        this.discussionService = discussionService;
        this.discussionCache = discussionCache;
    }

    @Override protected DiscussionKeyList fetch(DiscussionListKey key) throws Throwable
    {
        // TODO
        return null;
    }
}
