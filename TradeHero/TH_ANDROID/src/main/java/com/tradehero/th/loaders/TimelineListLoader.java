package com.tradehero.th.loaders;

import android.content.Context;
import com.tradehero.common.persistence.Query;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.timeline.TimelineManager;
import com.tradehero.th.persistence.timeline.TimelineStore;
import com.tradehero.th.utils.DaggerUtils;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 9/12/13 Time: 11:37 AM Copyright (c) TradeHero */
public class TimelineListLoader extends PaginationListLoader<TimelineItem>
{
    private static final String TAG = TimelineListLoader.class.getSimpleName();

    private final UserBaseKey userBaseKey;

    private Integer upperItemId;
    private Integer lowerItemId;

    @Inject protected TimelineManager timelineManager;

    public TimelineListLoader(Context context, UserBaseKey userBaseKey)
    {
        super(context);
        this.userBaseKey = userBaseKey;
        DaggerUtils.inject(this);
    }

    @Override public List<TimelineItem> loadInBackground()
    {
        if (lowerItemId != null)
        {
            ++lowerItemId;
        }
        if (upperItemId != null)
        {
            --upperItemId;
        }
        THLog.d(TAG, "Start loading timeline with upperItemId=" + upperItemId + "/ lowerItemId=" + lowerItemId);

        Query query = new Query();
        query.setId(getOwnerId());
        query.setLower(lowerItemId);
        query.setUpper(upperItemId);
        query.setProperty(TimelineStore.PER_PAGE, getPerPage());

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

    @Override protected void onLoadNext(TimelineItem firstVisible)
    {
        if (firstVisible == null)
        {
            return;
        }

        upperItemId = null;
        lowerItemId = firstVisible.getTimelineItemId();
        forceLoad();
    }

    @Override protected void onLoadPrevious(TimelineItem startItem)
    {
        resetQuery();
        if (startItem != null)
        {
            upperItemId = startItem.getTimelineItemId();
        }
        forceLoad();
    }

    @Override protected void onReset()
    {
        super.onReset();
    }

    public void resetQuery()
    {
        upperItemId = null;
        lowerItemId = null;
    }

    public int getOwnerId()
    {
        return userBaseKey.key;
    }
}
