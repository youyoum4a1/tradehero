package com.tradehero.th.loaders;

import android.content.Context;
import com.tradehero.common.persistence.Query;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.persistence.timeline.TimelineManager;
import com.tradehero.th.persistence.timeline.TimelineStore;
import com.tradehero.th.utils.DaggerUtils;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 9/12/13 Time: 11:37 AM Copyright (c) TradeHero */
public class TimelinePagedItemListLoader extends PagedItemListLoader<TimelineItem>
{
    private static final String TAG = TimelinePagedItemListLoader.class.getSimpleName();

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
        query.setUpper(maxItemId);
        query.setProperty(TimelineStore.PER_PAGE, getItemsPerPage());

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

    @Override protected void onLoadNextPage(TimelineItem firstVisibleItem)
    {
        if (firstVisibleItem == null)
        {
            return;
        }

        maxItemId = null;
        minItemId = firstVisibleItem.getId();
        forceLoad();
    }

    @Override protected void onLoadPreviousPage(TimelineItem lastVisibleItem)
    {
        if (lastVisibleItem == null)
        {
            return;
        }

        minItemId = null;
        maxItemId = lastVisibleItem.getId();
        forceLoad();
    }

    @Override protected void onReset()
    {
        super.onReset();
    }

    public void resetQuery()
    {
        maxItemId = null;
        minItemId = null;
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
