package com.tradehero.th.api.discussion.key;

import android.os.Bundle;
import com.tradehero.th.api.discussion.DiscussionType;
import android.support.annotation.NonNull;

public class CommentKey extends DiscussionKey<CommentKey>
{
    private static final DiscussionType TYPE = DiscussionType.COMMENT;

    //<editor-fold desc="Constructors">
    public CommentKey(@NonNull Integer id)
    {
        super(id);
    }

    protected CommentKey(@NonNull Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public DiscussionType getType()
    {
        return TYPE;
    }
}
