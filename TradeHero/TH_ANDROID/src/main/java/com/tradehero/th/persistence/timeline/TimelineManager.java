package com.tradehero.th.persistence.timeline;

import com.tradehero.common.cache.DatabaseCache;
import com.tradehero.common.persistence.Query;
import com.tradehero.th.api.timeline.TimelineItemDTOEnhanced;
import dagger.Lazy;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 9/26/13 Time: 6:07 PM Copyright (c) TradeHero */
public class TimelineManager
{
    @Inject DatabaseCache dbCache;
    @Inject Lazy<TimelineStore.Factory> allTimelineStores;

    public List<TimelineItemDTOEnhanced> getTimeline(Query query, boolean forceReload) throws IOException
    {
        // TODO scope locking for current timeline of user
        TimelineStore timelineStore = allTimelineStores.get().under((Integer) query.getId());
        timelineStore.setQuery(query);
        return forceReload ? dbCache.requestAndStore(timelineStore) : dbCache.loadOrRequest(timelineStore);
        // and unlock the scope
    }
}
