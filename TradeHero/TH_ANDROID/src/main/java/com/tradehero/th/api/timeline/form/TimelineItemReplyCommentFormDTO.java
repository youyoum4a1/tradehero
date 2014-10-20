package com.tradehero.th.api.timeline.form;

import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.form.ReplyDiscussionFormDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import org.jetbrains.annotations.NotNull;

public class TimelineItemReplyCommentFormDTO extends ReplyDiscussionFormDTO
{
    public static final DiscussionType TYPE = DiscussionType.TIMELINE_ITEM;

    public TimelineItemReplyCommentFormDTO()
    {
        super();
    }

    @Override @NotNull public DiscussionType getInReplyToType()
    {
        return TYPE;
    }

    @Override @NotNull public DiscussionKey getInitiatingDiscussionKey()
    {
        return new TimelineItemDTOKey(inReplyToId);
    }
}
