package com.tradehero.th.loaders;

import android.content.Context;
import com.android.internal.util.Predicate;
import com.tradehero.common.utils.CollectionUtils;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.timeline.TimelineSection;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.persistence.timeline.TimelineManager;
import com.tradehero.th.persistence.timeline.TimelineQuery;
import com.tradehero.th.persistence.timeline.TimelineStore;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class TimelineListLoader extends PaginationListLoader<TimelineItemDTO>
{
    private final UserBaseKey userBaseKey;
    private final TimelineSection section;

    private Integer upperItemId;
    private Integer lowerItemId;

    @Inject protected TimelineManager timelineManager;

    public TimelineListLoader(Context context, UserBaseKey userBaseKey, TimelineSection section)
    {
        super(context);
        this.userBaseKey = userBaseKey;
        this.section = section;
        HierarchyInjector.inject(context, this);
    }

    @Override public List<TimelineItemDTO> loadInBackground()
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
            List<TimelineItemDTO> timelineResult = timelineManager.getTimeline(query, true);
            if (timelineResult == null)
            {
                return null;
            }
            return CollectionUtils.filter(timelineResult, new Predicate<TimelineItemDTO>()
            {
                @Override public boolean apply(TimelineItemDTO timelineItemDTOKey)
                {
                    boolean condition = true;
                    if (lowerItemId != null)
                    {
                        condition = timelineItemDTOKey.id >= lowerItemId;
                    }
                    if (upperItemId != null)
                    {
                        condition &= timelineItemDTOKey.id <= upperItemId;
                    }
                    return condition;
                }
            });
        }
        catch (Exception e)
        {
            // TODO Exception come from loading timelines from database
            return null;
        }
    }

    @Override protected void onLoadNext(TimelineItemDTO firstVisible)
    {

        upperItemId = null;
        //TODO it's just a temp fix
        if (firstVisible != null)
        {
            lowerItemId = firstVisible.id;
        }
        forceLoad();
    }

    @Override protected void onLoadPrevious(TimelineItemDTO startItem)
    {
        resetQuery();
        if (startItem != null)
        {
            upperItemId = startItem.id;
        }
        forceLoad();
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
