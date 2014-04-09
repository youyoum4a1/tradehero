package com.tradehero.th.persistence.discussion;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionKey;
import com.tradehero.th.network.service.DiscussionService;
import com.tradehero.th.persistence.SingleCacheMaxSize;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by thonguyen on 4/4/14.
 */
@Singleton
public class DiscussionCache extends StraightDTOCache<DiscussionKey, DiscussionDTO>
{
    private final Lazy<DiscussionService> discussionService;

    @Inject public DiscussionCache(
            @SingleCacheMaxSize IntPreference maxSize,
            Lazy<DiscussionService> discussionService)
    {
        super(maxSize.get());

        this.discussionService = discussionService;
    }

    @Override protected DiscussionDTO fetch(DiscussionKey key) throws Throwable
    {
        // TODO
        return null;
    }
}
