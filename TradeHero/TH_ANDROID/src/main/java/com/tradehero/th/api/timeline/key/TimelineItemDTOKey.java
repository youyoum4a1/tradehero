package com.tradehero.th.api.timeline.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.DiscussionKey;

public class TimelineItemDTOKey extends DiscussionKey<TimelineItemDTOKey>
{
    private static final DiscussionType TYPE = DiscussionType.TIMELINE_ITEM;

    //<editor-fold desc="Constructors">
    public TimelineItemDTOKey(@NonNull Integer id)
    {
        super(id);
    }

    public TimelineItemDTOKey(@NonNull Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public DiscussionType getType()
    {
        return TYPE;
    }
}
