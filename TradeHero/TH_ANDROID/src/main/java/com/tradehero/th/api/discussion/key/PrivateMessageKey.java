package com.tradehero.th.api.discussion.key;

import android.os.Bundle;
import com.tradehero.th.api.discussion.DiscussionType;

public class PrivateMessageKey extends DiscussionKey
{
    private static DiscussionType TYPE = DiscussionType.PRIVATE_MESSAGE;

    public PrivateMessageKey(Integer id)
    {
        super(id);
    }

    protected PrivateMessageKey(Bundle args)
    {
        super(args);
    }

    @Override public DiscussionType getType()
    {
        return TYPE;
    }
}
