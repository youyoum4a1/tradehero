package com.tradehero.th.models.discussion;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOFactory;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.persistence.discussion.DiscussionCache;

public class DTOProcessorDiscussionCreate extends DTOProcessorDiscussion
{
    private final DiscussionCache discussionCache;
    private final DiscussionKey stubKey;

    public DTOProcessorDiscussionCreate(
            DiscussionDTOFactory discussionDTOFactory,
            DiscussionCache discussionCache, DiscussionKey stubKey)
    {
        super(discussionDTOFactory);
        this.discussionCache = discussionCache;
        this.stubKey = stubKey;
    }

    @Override public DiscussionDTO process(DiscussionDTO discussionDTO)
    {
        DiscussionDTO processed = super.process(discussionDTO);
        if (stubKey != null)
        {
            discussionCache.invalidate(stubKey);
        }
        if (processed != null)
        {
            discussionCache.put(processed.getDiscussionKey(), processed);
        }
        if (processed != null)
        {
            processed.stubKey = stubKey;
        }
        return processed;
    }
}
