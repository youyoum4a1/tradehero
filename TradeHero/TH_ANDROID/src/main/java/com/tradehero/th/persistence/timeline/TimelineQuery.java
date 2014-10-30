package com.tradehero.th.persistence.timeline;

import com.tradehero.common.persistence.Query;
import com.tradehero.th.api.timeline.TimelineSection;

/**
 * Created by tho on 9/12/2014.
 */
public class TimelineQuery extends Query
{
    private TimelineSection section;

    public void setSection(TimelineSection section)
    {
        this.section = section;
    }

    public TimelineSection getSection()
    {
        return section;
    }
}
