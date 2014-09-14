package com.tradehero.th.persistence.timeline;

import com.tradehero.common.persistence.Query;
import com.tradehero.th.network.service.UserTimelineService;

/**
 * Created by tho on 9/12/2014.
 */
public class TimelineQuery extends Query
{
    private UserTimelineService.TimelineSection section;

    public void setSection(UserTimelineService.TimelineSection section)
    {
        this.section = section;
    }

    public UserTimelineService.TimelineSection getSection()
    {
        return section;
    }
}
