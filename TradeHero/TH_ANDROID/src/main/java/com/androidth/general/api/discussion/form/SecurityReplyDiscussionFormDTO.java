package com.androidth.general.api.discussion.form;

import android.support.annotation.NonNull;
import com.androidth.general.api.discussion.DiscussionType;
import com.androidth.general.api.discussion.key.DiscussionKey;
import com.androidth.general.api.discussion.key.SecurityDiscussionKey;

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
