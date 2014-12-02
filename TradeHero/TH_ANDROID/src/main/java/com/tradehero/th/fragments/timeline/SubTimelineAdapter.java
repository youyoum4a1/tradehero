package com.tradehero.th.fragments.timeline;

import android.content.Context;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.loaders.TimelineListLoader;

public class SubTimelineAdapter extends LoaderDTOAdapter<TimelineItemDTO, TimelineItemViewLinear, TimelineListLoader>
{
    public SubTimelineAdapter(Context context, int timelineLoaderId, int layoutResourceId)
    {
        super(context, timelineLoaderId, layoutResourceId);
    }

    @Override protected void fineTune(int position, TimelineItemDTO dto, TimelineItemViewLinear dtoView)
    {
    }

    @Override public int getItemViewType(int position)
    {
        return MainTimelineAdapter.TIMELINE_ITEM_TYPE;
    }
}
