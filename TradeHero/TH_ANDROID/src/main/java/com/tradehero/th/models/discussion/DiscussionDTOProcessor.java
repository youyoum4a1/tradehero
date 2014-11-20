package com.tradehero.th.models.discussion;

import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.persistence.discussion.DiscussionCacheRx;
import rx.functions.Func1;

public class DiscussionDTOProcessor<T extends AbstractDiscussionCompactDTO> implements Func1<PaginatedDTO<T>, PaginatedDTO<T>>
{
    private final DiscussionCacheRx discussionCache;

    protected DiscussionDTOProcessor(DiscussionCacheRx discussionCache)
    {
        this.discussionCache = discussionCache;
    }

    @Override public PaginatedDTO<T> call(PaginatedDTO<T> paginatedDiscussionDTO)
    {
        if (paginatedDiscussionDTO != null)
        {
            for (T item: paginatedDiscussionDTO.getData())
            {
                discussionCache.onNext(item.getDiscussionKey(), item);
            }
        }
        return paginatedDiscussionDTO;
    }
}
