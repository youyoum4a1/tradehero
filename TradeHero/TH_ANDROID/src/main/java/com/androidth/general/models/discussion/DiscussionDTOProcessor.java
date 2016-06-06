package com.androidth.general.models.discussion;

import com.androidth.general.api.discussion.AbstractDiscussionCompactDTO;
import com.androidth.general.api.pagination.PaginatedDTO;
import com.androidth.general.persistence.discussion.DiscussionCacheRx;
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
