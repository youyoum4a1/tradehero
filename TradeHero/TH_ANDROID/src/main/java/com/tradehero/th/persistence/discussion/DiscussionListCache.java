package com.tradehero.th.persistence.discussion;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.PaginatedDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.DiscussionKeyList;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.network.service.DiscussionService;
import com.tradehero.th.persistence.ListCacheMaxSize;
import dagger.Lazy;
import java.util.List;
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

    @Override protected DiscussionKeyList fetch(DiscussionListKey discussionListKey) throws Throwable
    {
        PaginatedDTO<DiscussionDTO> paginatedDiscussionDTO =
                discussionService.get().getDiscussions(
                        discussionListKey.inReplyToType, discussionListKey.inReplyToId, discussionListKey.toMap());

        return putInternal(paginatedDiscussionDTO);
    }

    private DiscussionKeyList putInternal(PaginatedDTO<DiscussionDTO> paginatedDiscussionDTO)
    {
        List<DiscussionDTO> data = paginatedDiscussionDTO.getData();

        DiscussionKeyList discussionKeys = new DiscussionKeyList();

        for (DiscussionDTO discussionDTO: data)
        {
            DiscussionKey discussionKey = new DiscussionKey(discussionDTO.id);

            discussionCache.get().put(discussionKey, discussionDTO);
            discussionKeys.add(discussionKey);
        }
        return discussionKeys;
    }
}
