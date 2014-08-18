package com.tradehero.th.api.timeline.form;

import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.form.DiscussionFormDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;

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

    @Override public DiscussionKey getInitiatingDiscussionKey()
    {
        return new TimelineItemDTOKey(inReplyToId);
    }
}
