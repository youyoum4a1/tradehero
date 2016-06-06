package com.androidth.general.api.timeline.form;

import android.support.annotation.NonNull;
import com.androidth.general.api.discussion.DiscussionType;
import com.androidth.general.api.discussion.form.ReplyDiscussionFormDTO;
import com.androidth.general.api.discussion.key.DiscussionKey;
import com.androidth.general.api.timeline.key.TimelineItemDTOKey;

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
