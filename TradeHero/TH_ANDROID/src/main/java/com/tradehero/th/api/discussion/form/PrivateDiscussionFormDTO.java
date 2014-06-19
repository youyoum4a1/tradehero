package com.tradehero.th.api.discussion.form;

import com.tradehero.th.api.discussion.DiscussionType;

public class PrivateDiscussionFormDTO extends DiscussionFormDTO
{
    public static final DiscussionType TYPE = DiscussionType.PRIVATE_MESSAGE;

    public PrivateDiscussionFormDTO()
    {
        super();
    }

    @Override public DiscussionType getInReplyToType()
    {
        return TYPE;
    }
}
