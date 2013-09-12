package com.tradehero.th.loaders;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.api.local.TimelineItemBuilder;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.NetworkEngine;
import com.tradehero.th.network.service.UserTimelineService;
import com.tradehero.th.widget.timeline.TimelineItemView;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 9/12/13 Time: 11:37 AM Copyright (c) TradeHero */
public class TimelineItemListLoader extends ItemListLoader<List<TimelineItem>>
    implements PullToRefreshListView.OnRefreshListener<ListView>
{
    private int ownerId;

    public TimelineItemListLoader(Context context)
    {
        super(context);
    }

    @Override protected boolean shouldReload()
    {
        return true;
    }

    public void setOwnerId(int ownerId)
    {
        this.ownerId = ownerId;
    }

    public int getOwnerId()
    {
        return ownerId;
    }

    @Override public List<TimelineItem> loadInBackground()
    {
        TimelineDTO timelineDTO = NetworkEngine.createService(UserTimelineService.class).getTimeline(ownerId, 42);

        TimelineItemBuilder timelineBuilder = new TimelineItemBuilder(timelineDTO);
        timelineBuilder.buildFrom(timelineDTO);
        return timelineBuilder.getItems();
    }

    @Override public void onRefresh(PullToRefreshBase<ListView> refreshView)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
