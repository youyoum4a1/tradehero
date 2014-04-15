package com.tradehero.th.api.discussion.key;

import android.os.Bundle;
import com.tradehero.th.api.discussion.DiscussionType;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/12/14 Time: 6:08 PM Copyright (c) TradeHero
 */
public class BroadcastDiscussionKey extends DiscussionKey
{
    private static DiscussionType TYPE = DiscussionType.BROADCAST_MESSAGE;

    public BroadcastDiscussionKey(Integer id)
    {
        super(id);
    }

    protected BroadcastDiscussionKey(Bundle args)
    {
        super(args);
    }

    @Override public DiscussionType getType()
    {
        return TYPE;
    }
}
