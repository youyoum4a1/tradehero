package com.tradehero.th.api.news;

import android.os.Bundle;
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
        super(id);
    }

    public NewsItemDTOKey(Bundle args)
    {
        super(args);
    }

    @Override public DiscussionType getType()
    {
        return TYPE;
    }
}
