package com.tradehero.th.loaders;

import android.content.Context;
import android.view.View;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.api.local.TimelineItemBuilder;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.network.NetworkEngine;
import com.tradehero.th.network.service.UserTimelineService;
import java.util.List;
import retrofit.RetrofitError;

/** Created with IntelliJ IDEA. User: tho Date: 9/12/13 Time: 11:37 AM Copyright (c) TradeHero */
public class TimelinePagedItemListLoader extends PagedItemListLoader<TimelineItem>
{
    private static final String TAG = TimelinePagedItemListLoader.class.getSimpleName();

    private int ownerId;
    private Integer maxItemId;
    private Integer minItemId;
    private View postableView;

    public TimelinePagedItemListLoader(Context context)
    {
        super(context);
    }

    public TimelinePagedItemListLoader(Context context, View postableView)
    {
        super(context);
        this.postableView = postableView;
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
        TimelineDTO timelineDTO = null;
        try
        {
            timelineDTO = NetworkEngine.createService(UserTimelineService.class).getTimeline(ownerId, maxItemId, minItemId, itemsPerPage);
        }
        catch (RetrofitError e)
        {
            THToast.post(postableView, R.string.network_error);
            THLog.e(TAG, "Could not load timeline items", e);
        }

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
