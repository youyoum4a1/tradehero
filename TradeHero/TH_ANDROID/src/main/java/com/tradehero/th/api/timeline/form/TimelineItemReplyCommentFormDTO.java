package com.ayondo.academy.api.timeline.form;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.discussion.DiscussionType;
import com.ayondo.academy.api.discussion.form.ReplyDiscussionFormDTO;
import com.ayondo.academy.api.discussion.key.DiscussionKey;
import com.ayondo.academy.api.timeline.key.TimelineItemDTOKey;

public class TimelineItemReplyCommentFormDTO extends ReplyDiscussionFormDTO
{
    public static final DiscussionType TYPE = DiscussionType.TIMELINE_ITEM;

    @Override @NonNull public DiscussionType getInReplyToType()
    {
        return TYPE;
    }

    @Override @NonNull public DiscussionKey getInitiatingDiscussionKey()
    {
        return new TimelineItemDTOKey(inReplyToId);
    }
}
