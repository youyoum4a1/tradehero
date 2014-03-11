package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.loaders.TimelineListLoader;

public class SubTimelineAdapter extends LoaderDTOAdapter<TimelineItem, TimelineItemView, TimelineListLoader>
{
    public SubTimelineAdapter(Context context, LayoutInflater inflater, int timelineLoaderId, int layoutResourceId)
    {
        super(context, inflater, timelineLoaderId, layoutResourceId);
    }

    @Override protected void fineTune(int position, TimelineItem dto, TimelineItemView dtoView)
    {
    }

    @Override public int getItemViewType(int position)
    {
        return MainTimelineAdapter.TIMELINE_ITEM_TYPE;
    }
}
