package com.tradehero.th.api.discussion.key;

import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.discussion.DiscussionType;

/**
 * Created by xavier on 3/7/14.
 */
public class DiscussionKey implements DTOKey
{
    public final DiscussionType inReplyToType;
    public final int inReplyToId;

    public DiscussionKey(DiscussionType inReplyToType, int inReplyToId)
    {
        this.inReplyToType = inReplyToType;
        this.inReplyToId = inReplyToId;
    }

    @Override public int hashCode()
    {
        return (inReplyToType == null ? 0 : inReplyToType.hashCode()) ^
                Integer.valueOf(inReplyToId).hashCode();
    }

    @Override public boolean equals(Object other)
    {
        return other.getClass().equals(getClass()) && equals((DiscussionKey) other);
    }

    public boolean equals(DiscussionKey other)
    {
        return other.getClass().equals(getClass()) &&
                equalFields(other);

    }

    protected boolean equalFields(DiscussionKey other)
    {
        return other != null &&
                (inReplyToType == null ? other.inReplyToType == null : inReplyToType.equals(other.inReplyToType)) &&
                inReplyToId == other.inReplyToId;
    }
}
