package com.tradehero.th.api.news;

import android.os.Bundle;
import com.tradehero.th.api.discussion.key.DiscussionKey;

/**
 * Created by tho on 3/26/2014.
 */
public class NewsItemDTOKey extends DiscussionKey
{
    private static final String BUNDLE_KEY_KEY = NewsItemDTOKey.class.getName() + ".key";

    public NewsItemDTOKey(Integer key)
    {
        super(key);
    }

    public NewsItemDTOKey(Bundle args)
    {
        super(args);
    }

    public NewsItemDTOKey(DiscussionKey discussionKey)
    {
        super(discussionKey.key);
    }

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
