package com.tradehero.th.api.timeline.form;

import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.form.DiscussionFormDTO;

public class TimelineItemCommentFormDTO extends DiscussionFormDTO
{
    public static final DiscussionType TYPE = DiscussionType.TIMELINE_ITEM;

    public TimelineItemCommentFormDTO()
    {
        super();
    }

    @Override public DiscussionType getInReplyToType()
    {
        return TYPE;
    }
}
