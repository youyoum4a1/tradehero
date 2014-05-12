package com.tradehero.th.api.timeline.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.form.DiscussionFormDTO;


public class TimelineItemCommentFormDTO extends DiscussionFormDTO
{
    public static DiscussionType TYPE = DiscussionType.TIMELINE_ITEM;

    public TimelineItemCommentFormDTO()
    {
        super();
    }

    @Override public DiscussionType getInReplyToType()
    {
        return TYPE;
    }
}
