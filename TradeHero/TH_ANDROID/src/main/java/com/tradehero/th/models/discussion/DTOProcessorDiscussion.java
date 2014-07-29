package com.tradehero.th.models.discussion;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOFactory;
import com.tradehero.th.models.DTOProcessor;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorDiscussion implements DTOProcessor<DiscussionDTO>
{
    @NotNull private final DiscussionDTOFactory discussionDTOFactory;

    public DTOProcessorDiscussion(
            @NotNull DiscussionDTOFactory discussionDTOFactory)
    {
        this.discussionDTOFactory = discussionDTOFactory;
    }

    @Override public DiscussionDTO process(DiscussionDTO discussionDTO)
    {
        return discussionDTOFactory.createChildClass(discussionDTO);
    }
}
