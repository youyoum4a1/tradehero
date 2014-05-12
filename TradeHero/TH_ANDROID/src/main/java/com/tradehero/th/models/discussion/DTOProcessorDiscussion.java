package com.tradehero.th.models.discussion;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOFactory;
import com.tradehero.th.models.DTOProcessor;

public class DTOProcessorDiscussion implements DTOProcessor<DiscussionDTO>
{
    private final DiscussionDTOFactory discussionDTOFactory;

    public DTOProcessorDiscussion(
            DiscussionDTOFactory discussionDTOFactory)
    {
        this.discussionDTOFactory = discussionDTOFactory;
    }

    @Override public DiscussionDTO process(DiscussionDTO discussionDTO)
    {
        return discussionDTOFactory.createChildClass(discussionDTO);
    }
}
