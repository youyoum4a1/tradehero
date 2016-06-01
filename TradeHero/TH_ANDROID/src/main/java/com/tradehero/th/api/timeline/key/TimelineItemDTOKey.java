package com.ayondo.academy.api.timeline.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.ayondo.academy.api.discussion.DiscussionType;
import com.ayondo.academy.api.discussion.key.DiscussionKey;

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

    @NonNull @Override public DiscussionType getType()
    {
        return TYPE;
    }
}
