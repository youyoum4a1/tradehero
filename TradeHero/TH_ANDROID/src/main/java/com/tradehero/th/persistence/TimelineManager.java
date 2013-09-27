package com.tradehero.th.persistence;

import com.tradehero.common.cache.DatabaseCache;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 9/26/13 Time: 6:07 PM Copyright (c) TradeHero */
public class TimelineManager
{
    @Inject DatabaseCache dbCache;

    @Inject Lazy<TimelineStore.Factory> allTimelineStores;

    public TimelineManager()
    {
        DaggerUtils.inject(this);
    }

    public List<TimelineItem> getTimeline(TimelineStore.TimelineFilter filter, boolean forceReload) throws IOException
    {
        // TODO scope locking for current timeline of user
        TimelineStore timelineStore = allTimelineStores.get().under(filter.getOwnerId());
        timelineStore.setFilter(filter);
        return forceReload ? dbCache.requestAndStore(timelineStore) : dbCache.loadOrRequest(timelineStore);
        // and unlock the scope
    }
}
