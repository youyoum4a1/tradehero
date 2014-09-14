package com.tradehero.th.loaders;

import android.content.Context;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.network.service.UserTimelineService;
import com.tradehero.th.persistence.timeline.TimelineManager;
import com.tradehero.th.persistence.timeline.TimelineQuery;
import com.tradehero.th.persistence.timeline.TimelineStore;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class TimelineListLoader extends PaginationListLoader<TimelineItemDTOKey>
{
    private final UserBaseKey userBaseKey;
    private final UserTimelineService.TimelineSection section;

    private Integer upperItemId;
    private Integer lowerItemId;

    @Inject protected TimelineManager timelineManager;

    public TimelineListLoader(Context context, UserBaseKey userBaseKey, UserTimelineService.TimelineSection section)
    {
        super(context);
        this.userBaseKey = userBaseKey;
        this.section = section;
        HierarchyInjector.inject(context, this);
    }

    @Override public List<TimelineItemDTOKey> loadInBackground()
    {
        if (lowerItemId != null)
        {
            ++lowerItemId;
        }
        if (upperItemId != null)
        {
            --upperItemId;
        }
        // lowerItemId and upperItemId may be null!
        Timber.d("Start loading timeline with upperItemId=%d/lowerItemId=%d", upperItemId, lowerItemId);

        TimelineQuery query = new TimelineQuery();
        query.setSection(section);
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

    @Override protected void onLoadNext(TimelineItemDTOKey firstVisible)
    {

        upperItemId = null;
        //TODO it's just a temp fix
        if (firstVisible != null)
        {
            lowerItemId = firstVisible.id;
        }
        forceLoad();
    }

    @Override protected void onLoadPrevious(TimelineItemDTOKey startItem)
    {
        resetQuery();
        if (startItem != null)
        {
            upperItemId = startItem.id;
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
