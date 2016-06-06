package com.androidth.general.models.discussion;

import com.androidth.general.api.discussion.DiscussionDTO;
import com.androidth.general.api.discussion.DiscussionDTOFactory;
import com.androidth.general.models.ThroughDTOProcessor;

public class DTOProcessorDiscussion extends ThroughDTOProcessor<DiscussionDTO>
{
    @Override public DiscussionDTO process(DiscussionDTO discussionDTO)
    {
        return DiscussionDTOFactory.createChildClass(discussionDTO);
    }
}
