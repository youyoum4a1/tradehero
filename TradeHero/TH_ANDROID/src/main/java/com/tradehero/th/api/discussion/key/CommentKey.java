package com.tradehero.th.api.discussion.key;

import android.os.Bundle;
import com.tradehero.th.api.discussion.DiscussionType;
import org.jetbrains.annotations.NotNull;

public class CommentKey extends DiscussionKey
{
    private static final DiscussionType TYPE = DiscussionType.COMMENT;

    //<editor-fold desc="Constructors">
    public CommentKey(@NotNull Integer id)
    {
        super(id);
    }

    protected CommentKey(@NotNull Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public DiscussionType getType()
    {
        return TYPE;
    }
}
