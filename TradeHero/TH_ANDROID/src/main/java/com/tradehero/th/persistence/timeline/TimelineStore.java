package com.tradehero.th.persistence.timeline;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tradehero.common.persistence.PersistableResource;
import com.tradehero.common.persistence.Query;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTOEnhanced;
import com.tradehero.th.network.retrofit.BasicRetrofitErrorHandler;
import com.tradehero.th.network.service.UserTimelineService;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.inject.Inject;
import javax.inject.Provider;
import retrofit.RetrofitError;

/** Created with IntelliJ IDEA. User: tho Date: 9/26/13 Time: 6:10 PM Copyright (c) TradeHero */
public class TimelineStore implements PersistableResource<TimelineItemDTOEnhanced>
{
    public static final String PER_PAGE = "perpage";
    private Query query;

    @Inject UserTimelineService timelineService;
    @Inject TimelineCache timelineCache;

    @Override public List<TimelineItemDTOEnhanced> request()
    {
        if (query != null)
        {
            TimelineDTO timelineDTO = null;
            try
            {
                timelineDTO = timelineService.getTimeline((Integer) query.getId(),
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

                for (TimelineItemDTOEnhanced itemDTO: timelineDTO.getEnhancedItems())
                {
                    itemDTO.setUser(timelineDTO.getUserById(itemDTO.userId));
                    timelineCache.put(itemDTO.getTimelineKey(), itemDTO);
                }

                return timelineDTO.getEnhancedItems();
            }
        }

        return null;
    }

    @Override public void store(SQLiteDatabase db, List<TimelineItemDTOEnhanced> items)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public Cursor getCursor(SQLiteDatabase db)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public TimelineItemDTOEnhanced loadFrom(Cursor cursor)
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

        private Map<Integer, TimelineStore> stores;

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
