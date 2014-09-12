package com.tradehero.th.persistence.timeline;

import com.tradehero.common.cache.DatabaseCache;
import com.tradehero.common.persistence.Query;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import dagger.Lazy;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

@Singleton
public class TimelineManager
{
    private final DatabaseCache dbCache;
    private final Lazy<TimelineStore.Factory> allTimelineStores;

    @Inject TimelineManager(DatabaseCache dbCache, Lazy<TimelineStore.Factory> allTimelineStores)
    {
        this.dbCache = dbCache;
        this.allTimelineStores = allTimelineStores;
    }

    public List<TimelineItemDTOKey> getTimeline(Query query, boolean forceReload) throws IOException
    {
        if (query == null)
        {
            Timber.e(new NullPointerException("query was null"), "");
            return null;
        }
        // TODO scope locking for current timeline of user
        TimelineStore timelineStore = allTimelineStores.get().under((Integer) query.getId());
        if (query.getId() == null)
        {
            Timber.e(new NullPointerException("query.getId was null"), "");
            return null;
        }
        if (timelineStore == null)
        {
            Timber.e(new NullPointerException("timelineStore was null"), "");
            return null;
        }
        timelineStore.setQuery(query);
        return forceReload ? dbCache.requestAndStore(timelineStore) : dbCache.loadOrRequest(timelineStore);
        // and unlock the scope
    }
}
