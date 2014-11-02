package com.tradehero.th.models.discussion;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOFactory;
import com.tradehero.th.models.DTOProcessor;
import org.jetbrains.annotations.NotNull;
import rx.functions.Action1;

public class DTOProcessorDiscussion implements DTOProcessor<DiscussionDTO>, Action1<DiscussionDTO>
{
    @NotNull private final DiscussionDTOFactory discussionDTOFactory;

    //<editor-fold desc="Constructors">
    public DTOProcessorDiscussion(
            @NotNull DiscussionDTOFactory discussionDTOFactory)
    {
        this.discussionDTOFactory = discussionDTOFactory;
    }
    //</editor-fold>

    @Override public DiscussionDTO process(DiscussionDTO discussionDTO)
    {
        return discussionDTOFactory.createChildClass(discussionDTO);
    }

    @Override public void call(DiscussionDTO discussionDTO)
    {
        process(discussionDTO);
    }
}
