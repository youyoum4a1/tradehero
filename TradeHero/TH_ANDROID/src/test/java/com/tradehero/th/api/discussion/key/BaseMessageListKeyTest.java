package com.tradehero.th.api.discussion.key;

import com.tradehero.th.api.discussion.key.MessageListKey;

/**
 * Created by xavier2 on 2014/4/11.
 */
public class BaseMessageListKeyTest
{
    protected MessageListKey key1_1()
    {
        return new MessageListKey(1, 1);
    }

    protected MessageListKey key1_2()
    {
        return new MessageListKey(1, 2);
    }

    protected MessageListKey key2_1()
    {
        return new MessageListKey(2, 1);
    }

    protected MessageListKey key2_2()
    {
        return new MessageListKey(2, 2);
    }
}
