package com.tradehero.th.models.discussion;

import android.support.annotation.NonNull;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOFactory;
import com.tradehero.th.models.ThroughDTOProcessor;

public class DTOProcessorDiscussion extends ThroughDTOProcessor<DiscussionDTO>
{
    @NonNull private final DiscussionDTOFactory discussionDTOFactory;

    //<editor-fold desc="Constructors">
    public DTOProcessorDiscussion(
            @NonNull DiscussionDTOFactory discussionDTOFactory)
    {
        this.discussionDTOFactory = discussionDTOFactory;
    }
    //</editor-fold>

    @Override public DiscussionDTO process(DiscussionDTO discussionDTO)
    {
        return discussionDTOFactory.createChildClass(discussionDTO);
    }
}
