package com.tradehero.th.api.timeline;

import android.os.Bundle;
import com.tradehero.th.api.discussion.key.DiscussionKey;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/11/14 Time: 11:22 AM Copyright (c) TradeHero
 */
public class TimelineItemDTOKey extends DiscussionKey
{
    private static final String BUNDLE_KEY_KEY = TimelineItemDTOKey.class.getName() + ".key";

    public TimelineItemDTOKey(Integer key)
    {
        super(key);
    }

    public TimelineItemDTOKey(Bundle args)
    {
        super(args);
    }

    public TimelineItemDTOKey(DiscussionKey discussionKey)
    {
        this(discussionKey.key);
    }

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
