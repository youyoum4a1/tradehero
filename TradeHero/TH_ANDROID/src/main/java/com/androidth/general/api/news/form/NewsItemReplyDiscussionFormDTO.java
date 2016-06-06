package com.androidth.general.api.news.form;

import android.support.annotation.NonNull;
import com.androidth.general.api.discussion.DiscussionType;
import com.androidth.general.api.discussion.form.ReplyDiscussionFormDTO;
import com.androidth.general.api.discussion.key.DiscussionKey;
import com.androidth.general.api.news.key.NewsItemDTOKey;

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
