package com.tradehero.th.api.news;

import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.DiscussionKey;

/**
 * Created by tho on 3/26/2014.
 */
public class NewsItemDTOKey extends DiscussionKey
{
    private static DiscussionType TYPE = DiscussionType.NEWS;

    public NewsItemDTOKey(Integer id)
    {
        super(TYPE, id);
    }
}
