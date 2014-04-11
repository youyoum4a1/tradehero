package com.tradehero.th.api.discussion.key;

import com.tradehero.th.api.discussion.DiscussionType;

/**
 * Created by xavier2 on 2014/4/10.
 */
public class TypedMessageListKey extends MessageListKey
{
    public final DiscussionType discussionType;

    //<editor-fold desc="Constructors">
    public TypedMessageListKey(MessageListKey other, DiscussionType discussionType)
    {
        super(other);
        this.discussionType = discussionType;
    }

    public TypedMessageListKey(Integer page, Integer perPage, DiscussionType discussionType)
    {
        super(page, perPage);
        this.discussionType = discussionType;
    }

    public TypedMessageListKey(Integer page, DiscussionType discussionType)
    {
        super(page);
        this.discussionType = discussionType;
    }

    protected TypedMessageListKey()
    {
        super();
        this.discussionType = null;
    }
    //</editor-fold>

    @Override public boolean equalFields(MessageListKey other)
    {
        return super.equals(other)
                && equalFields((TypedMessageListKey) other);
    }

    public boolean equalFields(TypedMessageListKey other)
    {
        return super.equalFields(other) &&
                (this.discussionType == null ? other.discussionType == null : this.discussionType.equals(other.discussionType));
    }
}
