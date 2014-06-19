package com.tradehero.th.api.discussion.key;

import android.os.Bundle;
import com.tradehero.th.api.discussion.DiscussionType;

public class CommentKey extends DiscussionKey
{
    private static final DiscussionType TYPE = DiscussionType.COMMENT;

    //<editor-fold desc="Constructors">
    public CommentKey(Integer id)
    {
        super(id);
    }

    protected CommentKey(Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public DiscussionType getType()
    {
        return TYPE;
    }
}
