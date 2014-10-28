package com.tradehero.th.models.discussion;

import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import rx.functions.Action1;

/**
 * Created by thonguyen on 28/10/14.
 */
public class DiscussionDTOProcessor<T extends AbstractDiscussionCompactDTO> implements Action1<PaginatedDTO<T>>
{
    private final DiscussionCache discussionCache;

    protected DiscussionDTOProcessor(DiscussionCache discussionCache)
    {
        this.discussionCache = discussionCache;
    }

    @Override public void call(PaginatedDTO<T> paginatedDiscussionDTO)
    {
        if (paginatedDiscussionDTO != null)
        {
            for (T item: paginatedDiscussionDTO.getData())
            {
                discussionCache.put(item.getDiscussionKey(), item);
            }
        }
    }
}
