package com.tradehero.th.api.discussion.key;

import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.Querylizable;
import com.tradehero.th.api.discussion.DiscussionType;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xavier on 3/7/14.
 */
public class DiscussionListKey
        implements DTOKey, Querylizable<String>
{
    public final DiscussionType inReplyToType;
    public final int inReplyToId;

    public DiscussionListKey(DiscussionType inReplyToType, int inReplyToId)
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
        return other.getClass().equals(getClass()) && equals((DiscussionListKey) other);
    }

    public boolean equals(DiscussionListKey other)
    {
        return other.getClass().equals(getClass()) &&
                equalFields(other);

    }

    protected boolean equalFields(DiscussionListKey other)
    {
        return other != null &&
                (inReplyToType == null ? other.inReplyToType == null : inReplyToType.equals(other.inReplyToType)) &&
                inReplyToId == other.inReplyToId;
    }

    @Override public Map<String, Object> toMap()
    {
        return new HashMap<>();
    }
}
