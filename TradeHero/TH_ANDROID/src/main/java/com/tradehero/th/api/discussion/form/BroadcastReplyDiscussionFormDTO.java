package com.tradehero.th.api.discussion.form;

import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.BroadcastDiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import org.jetbrains.annotations.NotNull;

public class BroadcastReplyDiscussionFormDTO extends ReplyDiscussionFormDTO
{
    public static final DiscussionType TYPE = DiscussionType.BROADCAST_MESSAGE;

    public BroadcastReplyDiscussionFormDTO()
    {
        super();
    }

    @Override @NotNull public DiscussionType getInReplyToType()
    {
        return TYPE;
    }

    @Override @NotNull public DiscussionKey getInitiatingDiscussionKey()
    {
        return new BroadcastDiscussionKey(inReplyToId);
    }
}
