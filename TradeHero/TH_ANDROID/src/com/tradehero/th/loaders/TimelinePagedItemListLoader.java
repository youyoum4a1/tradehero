package com.tradehero.th.loaders;

import android.content.Context;
import android.widget.AbsListView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.api.local.TimelineItemBuilder;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.network.NetworkEngine;
import com.tradehero.th.network.service.UserTimelineService;
import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 9/12/13 Time: 11:37 AM Copyright (c) TradeHero */
public class TimelinePagedItemListLoader extends PagedItemListLoader<TimelineItem>
{
    private int ownerId;

    public TimelinePagedItemListLoader(Context context)
    {
        super(context);
    }

    @Override public List<TimelineItem> loadInBackground()
    {
        int maxItemId = getLastVisibleItem() == null ? 0 : getLastVisibleItem().getId();
        TimelineDTO timelineDTO = NetworkEngine.createService(UserTimelineService.class).getTimeline(ownerId, maxItemId, 10);

        TimelineItemBuilder timelineBuilder = new TimelineItemBuilder(timelineDTO);
        return timelineBuilder.getItems();
    }

    @Override protected boolean shouldReload()
    {
        return true;
    }

    @Override protected void onLoadNextPage(TimelineItem lastItemId)
    {
        forceLoad();
    }

    @Override protected void onLoadPreviousPage(TimelineItem startItemId)
    {
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
