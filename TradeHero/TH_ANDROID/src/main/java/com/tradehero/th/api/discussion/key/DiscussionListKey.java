package com.tradehero.th.api.discussion.key;

import android.os.Bundle;
import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.Querylizable;
import com.tradehero.th.api.discussion.DiscussionType;
import java.util.HashMap;
import java.util.Map;

public class DiscussionListKey
        implements DTOKey, Querylizable<String>
{
    public static final String IN_REPLY_TO_TYPE_NAME_BUNDLE_KEY = DiscussionListKey.class.getName() + ".inReplyToType";
    public static final String IN_REPLY_TO_ID_BUNDLE_KEY = DiscussionListKey.class.getName() + ".inReplyToId";

    public final DiscussionType inReplyToType;
    public final int inReplyToId;

    //<editor-fold desc="Constructors">
    public DiscussionListKey(DiscussionType inReplyToType, int inReplyToId)
    {
        this.inReplyToType = inReplyToType;
        this.inReplyToId = inReplyToId;
    }

    public DiscussionListKey(Bundle args)
    {
        if (!args.containsKey(IN_REPLY_TO_TYPE_NAME_BUNDLE_KEY))
        {
            throw new IllegalArgumentException("Missing IN_REPLY_TO_TYPE_NAME_BUNDLE_KEY");
        }
        if (!args.containsKey(IN_REPLY_TO_ID_BUNDLE_KEY))
        {
            throw new IllegalArgumentException("Missing IN_REPLY_TO_ID_BUNDLE_KEY");
        }
        this.inReplyToType = DiscussionType.valueOf(args.getString(IN_REPLY_TO_TYPE_NAME_BUNDLE_KEY));
        this.inReplyToId = args.getInt(IN_REPLY_TO_ID_BUNDLE_KEY);
    }
    //</editor-fold>

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

    public Bundle getArgs()
    {
        Bundle args = new Bundle();
        putParameters(args);
        return args;
    }

    protected void putParameters(Bundle args)
    {
        args.putString(IN_REPLY_TO_TYPE_NAME_BUNDLE_KEY, inReplyToType.name());
        args.putInt(IN_REPLY_TO_ID_BUNDLE_KEY, inReplyToId);
    }

    @Override public Map<String, Object> toMap()
    {
        return new HashMap<>();
    }
}
