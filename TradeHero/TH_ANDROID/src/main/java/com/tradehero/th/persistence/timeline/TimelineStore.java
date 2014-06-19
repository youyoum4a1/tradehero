package com.tradehero.th.persistence.timeline;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tradehero.common.persistence.PersistableResource;
import com.tradehero.common.persistence.Query;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.retrofit.BasicRetrofitErrorHandler;
import com.tradehero.th.network.service.UserTimelineServiceWrapper;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.inject.Inject;
import javax.inject.Provider;
import retrofit.RetrofitError;

public class TimelineStore implements PersistableResource<TimelineItemDTOKey>
{
    public static final String PER_PAGE = "perpage";
    private Query query;

    @Inject UserTimelineServiceWrapper timelineServiceWrapper;
    @Inject DiscussionCache discussionCache;

    @Override public List<TimelineItemDTOKey> request()
    {
        if (query != null)
        {
            TimelineDTO timelineDTO = null;
            try
            {
                timelineDTO = timelineServiceWrapper.getTimeline(
                        new UserBaseKey((Integer) query.getId()),
                        (Integer) query.getProperty(PER_PAGE),
                        query.getUpper(),
                        query.getLower());
            }
            catch (RetrofitError retrofitError)
            {
                BasicRetrofitErrorHandler.handle(retrofitError);
            }

            if (timelineDTO != null)
            {

                List<TimelineItemDTOKey> timelineItemDTOKeys = new ArrayList<>();
                if (timelineDTO.getEnhancedItems() != null)
                {
                    for (TimelineItemDTO itemDTO: timelineDTO.getEnhancedItems())
                    {
                        itemDTO.setUser(timelineDTO.getUserById(itemDTO.userId));
                        TimelineItemDTOKey timelineKey = itemDTO.getDiscussionKey();
                        discussionCache.put(timelineKey, itemDTO);
                        timelineItemDTOKeys.add(timelineKey);

                    }
                }

                return timelineItemDTOKeys;
            }
        }

        return null;
    }

    @Override public void store(SQLiteDatabase db, List<TimelineItemDTOKey> items)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public Cursor getCursor(SQLiteDatabase db)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public TimelineItemDTOKey loadFrom(Cursor cursor)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setQuery(Query query)
    {
        this.query = query;
    }

    // TODO guice has very nice feature that inject a factory using annotation @Factory
    // need to change this into interface when dagger has similar feature, for now, it's hack :v
    public static class Factory
    {
        @Inject Provider<TimelineStore> timelineStoreProviders;

        private final Map<Integer, TimelineStore> stores;

        public Factory()
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
