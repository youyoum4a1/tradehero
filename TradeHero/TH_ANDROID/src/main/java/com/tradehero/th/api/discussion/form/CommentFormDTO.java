package com.tradehero.th.api.discussion.form;

import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.CommentKey;
import com.tradehero.th.api.discussion.key.DiscussionKey;

public class CommentFormDTO extends DiscussionFormDTO
{
    public static final DiscussionType TYPE = DiscussionType.COMMENT;

    public CommentFormDTO()
    {
        super();
    }

    @Override public DiscussionType getInReplyToType()
    {
        return TYPE;
    }

    @Override public DiscussionKey getInitiatingDiscussionKey()
    {
        return new CommentKey(inReplyToId);
    }
}
