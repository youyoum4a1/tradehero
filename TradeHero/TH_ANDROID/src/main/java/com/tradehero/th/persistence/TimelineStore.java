package com.tradehero.th.persistence;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tradehero.common.persistence.PersistableResource;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.api.local.TimelineItemBuilder;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.network.NetworkEngine;
import com.tradehero.th.network.service.UserTimelineService;
import com.tradehero.th.utils.DaggerUtils;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.inject.Inject;
import javax.inject.Provider;

/** Created with IntelliJ IDEA. User: tho Date: 9/26/13 Time: 6:10 PM Copyright (c) TradeHero */
public class TimelineStore implements PersistableResource<TimelineItem>
{
    private TimelineFilter filter;

    @Override public List<TimelineItem> request()
    {
        if (filter != null)
        {
            TimelineDTO timelineDTO = NetworkEngine.createService(UserTimelineService.class)
                    .getTimeline(filter.getOwnerId(), filter.getMaxId(), filter.getMinId(), filter.getPerPage());

            TimelineItemBuilder timelineBuilder = new TimelineItemBuilder(timelineDTO);
            return timelineBuilder.getItems();
        }

        return null;
    }

    @Override public void store(SQLiteDatabase db, List<TimelineItem> items)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public Cursor getCursor(SQLiteDatabase db)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public TimelineItem loadFrom(Cursor cursor)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setFilter(TimelineFilter filter)
    {
        this.filter = filter;
    }

    // TODO guice has very nice feature that inject a factory using annotation @Factory
    // need to change this into interface when dagger has similar feature, for now, it's hack :v
    public static class Factory
    {
        @Inject Provider<TimelineStore> timelineStoreProviders;

        private Map<Integer, TimelineStore> stores;

        public Factory()
        {
            stores = new WeakHashMap<>();
            DaggerUtils.inject(this);
        }

        public TimelineStore under(int userId)
        {
            if (stores.get(userId) == null)
            {
                stores.put(userId, timelineStoreProviders.get());
            }
            return stores.get(userId);
        }
    }

    public static class TimelineFilter extends PaginationFilter
    {
        private int ownerId;

        public TimelineFilter(int ownerId, Comparable maxItemId, Comparable minItemId, int itemsPerPage)
        {
            super(maxItemId, minItemId, itemsPerPage);
            this.ownerId = ownerId;
        }

        public int getOwnerId()
        {
            return ownerId;
        }

        public void setOwnerId(int ownerId)
        {
            this.ownerId = ownerId;
        }
    }
}
