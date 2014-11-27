package com.tradehero.th.persistence.timeline;

import com.tradehero.common.persistence.Query;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import dagger.Lazy;
import timber.log.Timber;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TimelineManager
{
    @Inject Lazy<TimelineStore.Factory> allTimelineStores;

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
        return new ArrayList<TimelineItemDTOKey>();
        // and unlock the scope
    }
}
