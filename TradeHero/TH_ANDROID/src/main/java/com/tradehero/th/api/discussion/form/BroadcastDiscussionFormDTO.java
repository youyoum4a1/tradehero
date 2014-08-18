package com.tradehero.th.api.discussion.form;

import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.BroadcastDiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionKey;

public class BroadcastDiscussionFormDTO extends DiscussionFormDTO
{
    public static final DiscussionType TYPE = DiscussionType.BROADCAST_MESSAGE;

    public BroadcastDiscussionFormDTO()
    {
        super();
    }

    @Override public DiscussionType getInReplyToType()
    {
        return TYPE;
    }

    @Override public DiscussionKey getInitiatingDiscussionKey()
    {
        return new BroadcastDiscussionKey(inReplyToId);
    }
}
