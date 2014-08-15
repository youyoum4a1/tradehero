package com.tradehero.th.api.discussion.form;

import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.SecurityDiscussionKey;

public class SecurityDiscussionFormDTO extends DiscussionFormDTO
{
    public static final DiscussionType TYPE = DiscussionType.SECURITY;

    public SecurityDiscussionFormDTO()
    {
        super();
    }

    @Override public DiscussionType getInReplyToType()
    {
        return TYPE;
    }

    @Override public DiscussionKey getInitiatingDiscussionKey()
    {
        return new SecurityDiscussionKey(inReplyToId);
    }
}
