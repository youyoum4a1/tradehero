package com.tradehero.th.api.article;

import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.DiscussionKey;

public class ArticleInfoDTOKey extends DiscussionKey<ArticleInfoDTOKey>
{
    public ArticleInfoDTOKey(Integer key)
    {
        super(key);
    }

    @Override public DiscussionType getType()
    {
        return DiscussionType.ARTICLE;
    }
}
