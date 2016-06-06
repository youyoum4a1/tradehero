package com.androidth.general.api.discussion.form;

import android.support.annotation.NonNull;
import com.androidth.general.api.discussion.DiscussionType;
import com.androidth.general.api.discussion.key.BroadcastDiscussionKey;
import com.androidth.general.api.discussion.key.DiscussionKey;

public class BroadcastReplyDiscussionFormDTO extends ReplyDiscussionFormDTO
{
    public static final DiscussionType TYPE = DiscussionType.BROADCAST_MESSAGE;

    @Override @NonNull public DiscussionType getInReplyToType()
    {
        return TYPE;
    }

    @Override @NonNull public DiscussionKey getInitiatingDiscussionKey()
    {
        return new BroadcastDiscussionKey(inReplyToId);
    }
}
