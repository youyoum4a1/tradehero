package com.tradehero.th.api.discussion.form;

import com.tradehero.th.api.discussion.DiscussionType;

public class BroadcastDiscussionFormDTO extends DiscussionFormDTO
{
    public static DiscussionType TYPE = DiscussionType.BROADCAST_MESSAGE;

    public BroadcastDiscussionFormDTO()
    {
        super();
    }

    @Override public DiscussionType getInReplyToType()
    {
        return TYPE;
    }
}
