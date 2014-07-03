package com.tradehero.th.api.news.form;

import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.form.DiscussionFormDTO;

public class NewsItemDiscussionFormDTO extends DiscussionFormDTO
{
    public static final DiscussionType TYPE = DiscussionType.NEWS;

    public NewsItemDiscussionFormDTO()
    {
        super();
    }

    @Override public DiscussionType getInReplyToType()
    {
        return TYPE;
    }
}
