package com.tradehero.th.fragments.discussion;

import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.timeline.TimelineCache;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/11/14 Time: 11:48 AM Copyright (c) TradeHero
 */
public class TimelineDiscussion extends DashboardFragment
{
    @Inject TimelineCache timelineCache;

    @Override public boolean isTabBarVisible()
    {
        return false;
    }
}
