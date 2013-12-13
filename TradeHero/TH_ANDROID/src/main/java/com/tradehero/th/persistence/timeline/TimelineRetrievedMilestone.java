package com.tradehero.th.persistence.timeline;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.Loader;
import com.tradehero.common.milestone.LoaderMilestone;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.loaders.PaginationListLoader;
import com.tradehero.th.loaders.TimelineListLoader;

/**
 * Created with IntelliJ IDEA. User: tho Date: 12/5/13 Time: 5:30 PM Copyright (c) TradeHero
 */
public abstract class TimelineRetrievedMilestone extends LoaderMilestone<TimelineItem>
{
    private final UserBaseKey userBaseKey;
    private PaginationListLoader<TimelineItem> timelineLoader;

    public TimelineRetrievedMilestone(Context context, UserBaseKey userBaseKey)
    {
        super(context);
        this.userBaseKey = userBaseKey;
    }

    @Override public void launch()
    {
        super.launch();
    }
}
