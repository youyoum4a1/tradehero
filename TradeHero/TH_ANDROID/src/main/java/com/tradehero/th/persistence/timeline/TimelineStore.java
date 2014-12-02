package com.tradehero.th.persistence.timeline;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tradehero.common.persistence.PersistableResource;
import com.tradehero.common.persistence.Query;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.UserTimelineServiceWrapper;
import com.tradehero.th.persistence.discussion.DiscussionCacheRx;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.inject.Inject;
import javax.inject.Provider;

public class TimelineStore implements PersistableResource<TimelineItemDTO>
{
    public static final String PER_PAGE = "perpage";
    private TimelineQuery query;

    private final UserTimelineServiceWrapper timelineServiceWrapper;
    private final DiscussionCacheRx discussionCache;

    @Inject public TimelineStore(UserTimelineServiceWrapper timelineServiceWrapper, DiscussionCacheRx discussionCache)
    {
        this.timelineServiceWrapper = timelineServiceWrapper;
        this.discussionCache = discussionCache;
    }

    @Override public List<TimelineItemDTO> request()
    {
        TimelineDTO timelineDTO = timelineServiceWrapper.getTimelineBySection(
                query.getSection(),
                new UserBaseKey((Integer) query.getId()),
                (Integer) query.getProperty(PER_PAGE),
                query.getUpper(),
                query.getLower());

        List<TimelineItemDTO> timelineItemDTOs = timelineDTO.getEnhancedItems();
        if (timelineItemDTOs != null)
        {
            for (TimelineItemDTO itemDTO: timelineItemDTOs)
            {
                itemDTO.setUser(timelineDTO.getUserById(itemDTO.userId));
                TimelineItemDTOKey timelineKey = itemDTO.getDiscussionKey();
                discussionCache.onNext(timelineKey, itemDTO);
            }
        }

        return timelineItemDTOs;
    }

    @Override public void store(SQLiteDatabase db, List<TimelineItemDTO> items)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public Cursor getCursor(SQLiteDatabase db)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public TimelineItemDTO loadFrom(Cursor cursor)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setQuery(Query query)
    {
        this.query = (TimelineQuery) query;
    }

    // TODO guice has very nice feature that inject a factory using annotation @Factory
    // need to change this into interface when dagger has similar feature, for now, it's hack :v
    public static class Factory
    {
        @Inject Provider<TimelineStore> timelineStoreProviders;

        private final Map<Integer, TimelineStore> stores;

        @Inject public Factory()
        {
            stores = new WeakHashMap<>();
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
}
