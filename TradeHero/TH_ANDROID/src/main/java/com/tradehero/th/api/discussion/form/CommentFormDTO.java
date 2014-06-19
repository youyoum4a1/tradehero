package com.tradehero.th.api.discussion.form;

import com.tradehero.th.api.discussion.DiscussionType;

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
}
