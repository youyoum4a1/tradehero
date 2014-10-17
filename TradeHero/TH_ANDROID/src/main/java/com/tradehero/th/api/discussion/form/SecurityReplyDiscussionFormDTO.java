package com.tradehero.th.api.discussion.form;

import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.SecurityDiscussionKey;
import org.jetbrains.annotations.NotNull;

public class SecurityReplyDiscussionFormDTO extends ReplyDiscussionFormDTO
{
    public static final DiscussionType TYPE = DiscussionType.SECURITY;

    public SecurityReplyDiscussionFormDTO()
    {
        super();
    }

    @Override @NotNull public DiscussionType getInReplyToType()
    {
        return TYPE;
    }

    @Override @NotNull public DiscussionKey getInitiatingDiscussionKey()
    {
        return new SecurityDiscussionKey(inReplyToId);
    }
}
