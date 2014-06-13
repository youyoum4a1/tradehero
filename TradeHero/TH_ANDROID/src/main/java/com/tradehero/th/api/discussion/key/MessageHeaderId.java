package com.tradehero.th.api.discussion.key;

import android.os.Bundle;
import com.tradehero.common.persistence.DTOKey;

public class MessageHeaderId implements DTOKey
{
    private static final String BUNDLE_KEY_COMMENT_ID = MessageHeaderId.class.getName() + ".commentId";

    public final int commentId;

    public static void putCommentId(Bundle args, int commentId)
    {
        args.putInt(BUNDLE_KEY_COMMENT_ID, commentId);
    }

    public static int getCommentId(Bundle args)
    {
        return args.getInt(BUNDLE_KEY_COMMENT_ID);
    }

    //<editor-fold desc="Constructors">
    public MessageHeaderId(int commentId)
    {
        this.commentId = commentId;
    }

    public MessageHeaderId(Bundle args)
    {
        this.commentId = getCommentId(args);
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return Integer.valueOf(commentId).hashCode();
    }

    @Override public boolean equals(Object other)
    {
        return equalClass(other) && equalFields((MessageHeaderId) other);
    }

    protected boolean equalClass(Object other)
    {
        return other != null && getClass().equals(other.getClass());
    }

    protected boolean equalFields(MessageHeaderId other)
    {
        return other != null && commentId == other.commentId;
    }

    public Bundle getArgs()
    {
        Bundle args = new Bundle();
        populate(args);
        return args;
    }

    protected void populate(Bundle args)
    {
        putCommentId(args, commentId);
    }
}
