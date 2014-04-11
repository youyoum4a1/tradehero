package com.tradehero.th.api.timeline.key;

import android.os.Bundle;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.DiscussionKey;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/11/14 Time: 11:22 AM Copyright (c) TradeHero
 */
public class TimelineItemDTOKey extends DiscussionKey
{
    private static DiscussionType TYPE = DiscussionType.TIMELINE_ITEM;

    public TimelineItemDTOKey(Integer id)
    {
        super(id);
    }

    public TimelineItemDTOKey(Bundle args)
    {
        super(args);
    }

    @Override public DiscussionType getType()
    {
        return TYPE;
    }
}
