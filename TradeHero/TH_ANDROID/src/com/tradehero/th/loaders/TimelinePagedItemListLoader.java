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
    private Integer maxItemId;
    private Integer minItemId;

    public TimelinePagedItemListLoader(Context context)
    {
        super(context);
    }

    @Override public List<TimelineItem> loadInBackground()
    {
        if (minItemId != null) {
            ++minItemId;
        }
        if (maxItemId != null) {
            --maxItemId;
        }
        TimelineDTO timelineDTO = NetworkEngine.createService(UserTimelineService.class).getTimeline(ownerId, maxItemId, minItemId, itemsPerPage);

        TimelineItemBuilder timelineBuilder = new TimelineItemBuilder(timelineDTO);
        return timelineBuilder.getItems();
    }

    @Override protected boolean shouldReload()
    {
        return true;
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
