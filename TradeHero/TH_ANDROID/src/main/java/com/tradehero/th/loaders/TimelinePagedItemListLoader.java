package com.tradehero.th.loaders;

import android.content.Context;
import com.tradehero.common.persistence.Query;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.persistence.TimelineManager;
import com.tradehero.th.persistence.TimelineStore.TimelineFilter;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 9/12/13 Time: 11:37 AM Copyright (c) TradeHero */
public class TimelinePagedItemListLoader extends PagedItemListLoader<TimelineItem>
{
    private static final String TAG = TimelinePagedItemListLoader.class.getSimpleName();
    private static final String PER_PAGE = "perpage";

    private int ownerId;
    private Integer maxItemId;
    private Integer minItemId;

    @Inject TimelineManager timelineManager;

    public TimelinePagedItemListLoader(Context context)
    {
        super(context);
        DaggerUtils.inject(this);
    }

    @Override public List<TimelineItem> loadInBackground()
    {
        if (minItemId != null)
        {
            ++minItemId;
        }
        if (maxItemId != null)
        {
            --maxItemId;
        }
        THLog.d(TAG, "Start loading timeline with maxItemId=" + maxItemId + "/ minItemId=" + minItemId);

        Query query = new Query();
        query.setId(ownerId);
        query.setLower(minItemId);
        query.setLower(maxItemId);
        query.setProperty(PER_PAGE, itemsPerPage);

        try
        {
            return timelineManager.getTimeline(query, true);
        }
        catch (IOException e)
        {
            // TODO Exception come from loading timelines from database
            return null;
        }
    }

    @Override protected void onLoadNextPage(TimelineItem lastItemId)
    {
        maxItemId = null;
        minItemId = getFirstVisibleItem() == null ? lastItemId.getId() : getFirstVisibleItem().getId();
        forceLoad();
    }

    @Override protected void onLoadPreviousPage(TimelineItem firstItemId)
    {
        minItemId = null;
        maxItemId = getLastVisibleItem() == null ? firstItemId.getId() : getLastVisibleItem().getId();
        forceLoad();
    }

    public void setOwnerId(int ownerId)
    {
        this.ownerId = ownerId;
    }

    public int getOwnerId()
    {
        return ownerId;
    }
}
