package com.tradehero.th.api.discussion.form;

import android.support.annotation.NonNull;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.CommentKey;
import com.tradehero.th.api.discussion.key.DiscussionKey;

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
