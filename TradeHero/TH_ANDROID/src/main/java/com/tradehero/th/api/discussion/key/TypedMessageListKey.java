package com.tradehero.th.api.discussion.key;

import com.tradehero.th.api.discussion.DiscussionType;

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
        return equalClass(other)
                && equalFields((TypedMessageListKey) other);
    }

    public boolean equalFields(TypedMessageListKey other)
    {
        return super.equalFields(other) &&
                (this.discussionType == null ? other.discussionType == null : this.discussionType.equals(other.discussionType));
    }

    @Override public boolean equalListing(MessageListKey other)
    {
        return super.equalListing(other) &&
                (discussionType == null ?
                        ((TypedMessageListKey) other).discussionType == null :
                        discussionType.equals(((TypedMessageListKey) other).discussionType));
    }

    @Override public TypedMessageListKey prev()
    {
        if (this.page <= FIRST_PAGE)
        {
            return null;
        }
        return new TypedMessageListKey(this.page - 1, perPage, discussionType);
    }

    @Override public TypedMessageListKey next()
    {
        return new TypedMessageListKey(page + 1, perPage, discussionType);
    }
}
