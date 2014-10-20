package com.tradehero.th.api.discussion.form;

import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.PrivateMessageKey;
import org.jetbrains.annotations.NotNull;

public class PrivateReplyDiscussionFormDTO extends ReplyDiscussionFormDTO
{
    public static final DiscussionType TYPE = DiscussionType.PRIVATE_MESSAGE;

    public PrivateReplyDiscussionFormDTO()
    {
        super();
    }

    @Override @NotNull public DiscussionType getInReplyToType()
    {
        return TYPE;
    }

    @Override @NotNull public DiscussionKey getInitiatingDiscussionKey()
    {
        return new PrivateMessageKey(inReplyToId);
    }
}
