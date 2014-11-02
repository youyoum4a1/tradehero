package com.tradehero.th.models.discussion;

import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.persistence.discussion.DiscussionCacheRx;
import rx.functions.Action1;

public class DiscussionDTOProcessor<T extends AbstractDiscussionCompactDTO> implements Action1<PaginatedDTO<T>>
{
    private final DiscussionCacheRx discussionCache;

    protected DiscussionDTOProcessor(DiscussionCacheRx discussionCache)
    {
        this.discussionCache = discussionCache;
    }

    @Override public void call(PaginatedDTO<T> paginatedDiscussionDTO)
    {
        if (paginatedDiscussionDTO != null)
        {
            for (T item: paginatedDiscussionDTO.getData())
            {
                discussionCache.onNext(item.getDiscussionKey(), item);
            }
        }
    }
}
