package com.tradehero.th.api.discussion.key;

import android.os.Bundle;
import com.tradehero.th.api.discussion.DiscussionType;

public class BroadcastDiscussionKey extends DiscussionKey
{
    private static final DiscussionType TYPE = DiscussionType.BROADCAST_MESSAGE;

    //<editor-fold desc="Constructors">
    public BroadcastDiscussionKey(Integer id)
    {
        super(id);
    }

    protected BroadcastDiscussionKey(Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public DiscussionType getType()
    {
        return TYPE;
    }
}
