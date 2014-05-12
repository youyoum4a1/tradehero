package com.tradehero.th.api.timeline.key;

import android.os.Bundle;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.DiscussionKey;

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
