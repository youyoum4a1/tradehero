package com.tradehero.th.api.discussion.key;

import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.users.UserBaseKey;

/**
 * Created by xavier2 on 2014/4/10.
 */
public class RecipientTypedMessageListKey extends TypedMessageListKey
{
    public final UserBaseKey recipientId;

    //<editor-fold desc="Constructors">
    public RecipientTypedMessageListKey(MessageListKey other, DiscussionType discussionType, UserBaseKey recipientId)
    {
        super(other, discussionType);
        this.recipientId = recipientId;
    }

    public RecipientTypedMessageListKey(Integer page, Integer perPage, DiscussionType discussionType, UserBaseKey recipientId)
    {
        super(page, perPage, discussionType);
        this.recipientId = recipientId;
    }

    public RecipientTypedMessageListKey(Integer page, DiscussionType discussionType, UserBaseKey recipientId)
    {
        super(page, discussionType);
        this.recipientId = recipientId;
    }

    protected RecipientTypedMessageListKey()
    {
        super();
        this.recipientId = null;
    }
    //</editor-fold>

    @Override public boolean equalFields(TypedMessageListKey other)
    {
        return super.equalClass(other) &&
                equalFields((RecipientTypedMessageListKey) other);
    }

    public boolean equalFields(RecipientTypedMessageListKey other)
    {
        return super.equalFields(other) &&
                (this.recipientId == null ? other.recipientId == null : this.recipientId.equals(other.recipientId));
    }

    @Override public RecipientTypedMessageListKey prev()
    {
        if (this.page <= FIRST_PAGE)
        {
            return null;
        }
        return new RecipientTypedMessageListKey(this.page - 1, perPage, discussionType, recipientId);
    }

    @Override public RecipientTypedMessageListKey next()
    {
        return new RecipientTypedMessageListKey(this.page + 1, perPage, discussionType, recipientId);
    }
}
