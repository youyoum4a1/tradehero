package com.tradehero.th.api.timeline.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.form.DiscussionFormDTO;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 10:32 PM To change this template use File | Settings | File Templates. */
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
