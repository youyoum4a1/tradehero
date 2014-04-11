package com.tradehero.th.api.discussion;

import com.tradehero.th.api.discussion.key.MessageListKey;
import com.tradehero.th.api.discussion.key.TypedMessageListKey;

/**
 * Created by xavier2 on 2014/4/11.
 */
public class BaseTypedMessageListKeyTest extends BaseMessageListKeyTest
{
    protected TypedMessageListKey key1_1_p()
    {
        return new TypedMessageListKey(1, 1, DiscussionType.PRIVATE_MESSAGE);
    }

    protected TypedMessageListKey key1_2_p()
    {
        return new TypedMessageListKey(1, 2, DiscussionType.PRIVATE_MESSAGE);
    }

    protected TypedMessageListKey key2_1_p()
    {
        return new TypedMessageListKey(2, 1, DiscussionType.PRIVATE_MESSAGE);
    }

    protected TypedMessageListKey key2_2_p()
    {
        return new TypedMessageListKey(2, 2, DiscussionType.PRIVATE_MESSAGE);
    }

    protected TypedMessageListKey key1_1_c()
    {
        return new TypedMessageListKey(1, 1, DiscussionType.COMMENT);
    }

    protected TypedMessageListKey key1_2_c()
    {
        return new TypedMessageListKey(1, 2, DiscussionType.COMMENT);
    }

    protected TypedMessageListKey key2_1_c()
    {
        return new TypedMessageListKey(2, 1, DiscussionType.COMMENT);
    }

    protected TypedMessageListKey key2_2_c()
    {
        return new TypedMessageListKey(2, 2, DiscussionType.COMMENT);
    }
}
