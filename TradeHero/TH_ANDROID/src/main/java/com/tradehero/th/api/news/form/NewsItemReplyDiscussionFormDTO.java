package com.tradehero.th.api.news.form;

import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.form.ReplyDiscussionFormDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import org.jetbrains.annotations.NotNull;

public class NewsItemReplyDiscussionFormDTO extends ReplyDiscussionFormDTO
{
    public static final DiscussionType TYPE = DiscussionType.NEWS;

    public NewsItemReplyDiscussionFormDTO()
    {
        super();
    }

    @Override @NotNull public DiscussionType getInReplyToType()
    {
        return TYPE;
    }

    @Override @NotNull public DiscussionKey getInitiatingDiscussionKey()
    {
        return new NewsItemDTOKey(inReplyToId);
    }
}
