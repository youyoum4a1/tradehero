package com.tradehero.th.api.discussion.form;

import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.BroadcastDiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import android.support.annotation.NonNull;

public class BroadcastReplyDiscussionFormDTO extends ReplyDiscussionFormDTO
{
    public static final DiscussionType TYPE = DiscussionType.BROADCAST_MESSAGE;

    public BroadcastReplyDiscussionFormDTO()
    {
        super();
    }

    @Override @NonNull public DiscussionType getInReplyToType()
    {
        return TYPE;
    }

    @Override @NonNull public DiscussionKey getInitiatingDiscussionKey()
    {
        return new BroadcastDiscussionKey(inReplyToId);
    }
}
