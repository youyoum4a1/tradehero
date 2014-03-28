package com.tradehero.th.fragments.discussion;

import android.content.Context;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.timeline.TimelineItemDTOKey;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/25/14 Time: 2:57 PM Copyright (c) TradeHero
 */
public class TimelineCommentListLoader extends DiscussionListLoader
{
    public TimelineCommentListLoader(Context context, TimelineItemDTOKey timelineItemDTOKey)
    {
        super(context, DiscussionType.TIMELINE_ITEM, timelineItemDTOKey.key);
    }
}
