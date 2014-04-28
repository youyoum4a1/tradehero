package com.tradehero.th.api.discussion.key;

import android.os.Bundle;
import com.tradehero.th.api.discussion.DiscussionType;

public class CommentKey extends DiscussionKey
{
    private static DiscussionType TYPE = DiscussionType.COMMENT;

    public CommentKey(Integer id)
    {
        super(id);
    }

    protected CommentKey(Bundle args)
    {
        super(args);
    }

    @Override public DiscussionType getType()
    {
        return TYPE;
    }
}
