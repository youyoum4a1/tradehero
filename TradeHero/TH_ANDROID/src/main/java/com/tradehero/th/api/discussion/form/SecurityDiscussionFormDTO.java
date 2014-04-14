package com.tradehero.th.api.discussion.form;

import com.tradehero.th.api.discussion.DiscussionType;

public class SecurityDiscussionFormDTO extends DiscussionFormDTO
{
    public static DiscussionType TYPE = DiscussionType.SECURITY;

    public SecurityDiscussionFormDTO()
    {
        super();
    }

    @Override public DiscussionType getInReplyToType()
    {
        return TYPE;
    }
}
