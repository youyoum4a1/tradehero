package com.tradehero.th.models.discussion;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOFactory;
import com.tradehero.th.models.ThroughDTOProcessor;

public class DTOProcessorDiscussion extends ThroughDTOProcessor<DiscussionDTO>
{
    @Override public DiscussionDTO process(DiscussionDTO discussionDTO)
    {
        return DiscussionDTOFactory.createChildClass(discussionDTO);
    }
}
