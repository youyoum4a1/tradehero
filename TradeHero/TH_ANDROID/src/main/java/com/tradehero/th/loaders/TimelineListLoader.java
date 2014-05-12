package com.tradehero.th.loaders;

import android.content.Context;
import com.tradehero.common.persistence.Query;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.timeline.TimelineManager;
import com.tradehero.th.persistence.timeline.TimelineStore;
import com.tradehero.th.utils.DaggerUtils;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class TimelineListLoader extends PaginationListLoader<TimelineItemDTOKey>
{
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
