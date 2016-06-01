package com.ayondo.academy.models.discussion;

import com.ayondo.academy.api.discussion.DiscussionDTO;
import com.ayondo.academy.api.discussion.DiscussionDTOFactory;
import com.ayondo.academy.models.ThroughDTOProcessor;

public class DTOProcessorDiscussion extends ThroughDTOProcessor<DiscussionDTO>
{
    @Override public DiscussionDTO process(DiscussionDTO discussionDTO)
    {
        return DiscussionDTOFactory.createChildClass(discussionDTO);
    }
}
