package com.ayondo.academy.api.news.form;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.discussion.DiscussionType;
import com.ayondo.academy.api.discussion.form.ReplyDiscussionFormDTO;
import com.ayondo.academy.api.discussion.key.DiscussionKey;
import com.ayondo.academy.api.news.key.NewsItemDTOKey;

public class NewsItemReplyDiscussionFormDTO extends ReplyDiscussionFormDTO
{
    public static final DiscussionType TYPE = DiscussionType.NEWS;

    public NewsItemReplyDiscussionFormDTO()
    {
        super();
    }

    @Override @NonNull public DiscussionType getInReplyToType()
    {
        return TYPE;
    }

    @Override @NonNull public DiscussionKey getInitiatingDiscussionKey()
    {
        return new NewsItemDTOKey(inReplyToId);
    }
}
