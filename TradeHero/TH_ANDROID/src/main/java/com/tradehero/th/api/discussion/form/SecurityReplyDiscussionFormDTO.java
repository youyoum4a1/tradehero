package com.ayondo.academy.api.discussion.form;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.discussion.DiscussionType;
import com.ayondo.academy.api.discussion.key.DiscussionKey;
import com.ayondo.academy.api.discussion.key.SecurityDiscussionKey;

public class SecurityReplyDiscussionFormDTO extends ReplyDiscussionFormDTO
{
    public static final DiscussionType TYPE = DiscussionType.SECURITY;

    public SecurityReplyDiscussionFormDTO()
    {
        super();
    }

    @Override @NonNull public DiscussionType getInReplyToType()
    {
        return TYPE;
    }

    @Override @NonNull public DiscussionKey getInitiatingDiscussionKey()
    {
        return new SecurityDiscussionKey(inReplyToId);
    }
}
