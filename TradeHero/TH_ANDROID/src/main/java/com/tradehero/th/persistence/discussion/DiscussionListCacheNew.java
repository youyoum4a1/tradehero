package com.tradehero.th.persistence.discussion;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionKeyList;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.discussion.key.MessageDiscussionListKey;
import com.tradehero.th.api.discussion.key.PaginatedDiscussionListKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.persistence.ListCacheMaxSize;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DiscussionListCacheNew extends StraightDTOCacheNew<DiscussionListKey, DiscussionKeyList>
{
    private final DiscussionCache discussionCache;
    private final DiscussionServiceWrapper discussionServiceWrapper;

    @Inject public DiscussionListCacheNew(
            @ListCacheMaxSize IntPreference maxSize,
            DiscussionServiceWrapper discussionServiceWrapper,
            DiscussionCache discussionCache)
    {
        super(maxSize.get());

        this.discussionServiceWrapper = discussionServiceWrapper;
        this.discussionCache = discussionCache;
    }

    @Override public DiscussionKeyList fetch(DiscussionListKey discussionListKey) throws Throwable
    {
        if (discussionListKey instanceof MessageDiscussionListKey)
        {
            return putInternal(discussionServiceWrapper.getMessageThread((MessageDiscussionListKey) discussionListKey));
        }
        else if (discussionListKey instanceof PaginatedDiscussionListKey)
        {
            return putInternal(discussionServiceWrapper.getDiscussions((PaginatedDiscussionListKey) discussionListKey));
        }
        throw new IllegalStateException("Unhandled key " + discussionListKey);
    }

    private DiscussionKeyList putInternal(PaginatedDTO<DiscussionDTO> paginatedDTO)
    {
        List<DiscussionDTO> data = paginatedDTO.getData();

        discussionCache.put(data);

        DiscussionKeyList discussionKeyList = new DiscussionKeyList();
        for (AbstractDiscussionDTO abstractDiscussionDTO: data)
        {
            discussionKeyList.add(abstractDiscussionDTO.getDiscussionKey());
        }

        return discussionKeyList;
    }

    public void invalidateAllPagesFor(DiscussionKey discussionKey)
    {
        for (DiscussionListKey discussionListKey : new ArrayList<>(snapshot().keySet()))
        {
            if (discussionListKey.equivalentFields(discussionKey))
            {
                invalidate(discussionListKey);
            }
        }
    }
    /**
     * TODO right
     * @param discussionType
     */
    public void invalidateAllForDiscussionType(DiscussionType discussionType)
    {
        for (DiscussionListKey discussionListKey : new ArrayList<>(snapshot().keySet()))
        {
            if (discussionListKey.inReplyToType == discussionType)
            {
                invalidate(discussionListKey);
            }
        }
    }

    public static interface DiscussionKeyListListener extends Listener<DiscussionListKey, DiscussionKeyList>
    {
    }
}
