package com.ayondo.academy.api.discussion.form;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.discussion.DiscussionType;
import com.ayondo.academy.api.discussion.key.CommentKey;
import com.ayondo.academy.api.discussion.key.DiscussionKey;

public class ReplyCommentFormDTO extends ReplyDiscussionFormDTO
{
    public static final DiscussionType TYPE = DiscussionType.COMMENT;

    public ReplyCommentFormDTO()
    {
        super();
    }

    @Override @NonNull public DiscussionType getInReplyToType()
    {
        return TYPE;
    }

    @Override @NonNull public DiscussionKey getInitiatingDiscussionKey()
    {
        return new CommentKey(inReplyToId);
    }
}
