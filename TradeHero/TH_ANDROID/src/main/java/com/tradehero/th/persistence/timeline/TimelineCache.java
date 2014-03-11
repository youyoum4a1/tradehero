package com.tradehero.th.persistence.timeline;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.timeline.TimelineItemDTOEnhanced;
import com.tradehero.th.api.timeline.TimelineItemDTOKey;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/11/14 Time: 11:20 AM Copyright (c) TradeHero
 */
public class TimelineCache extends StraightDTOCache<TimelineItemDTOKey, TimelineItemDTOEnhanced>
{
    private static final int DEFAULT_MAX_SIZE = 200;

    @Inject public TimelineCache()
    {
        super(DEFAULT_MAX_SIZE);
    }

    @Override protected TimelineItemDTOEnhanced fetch(TimelineItemDTOKey key) throws Throwable
    {
        throw new IllegalStateException("You cannot fetch an individual TimelineItemDTOEnhanced");
    }
}
