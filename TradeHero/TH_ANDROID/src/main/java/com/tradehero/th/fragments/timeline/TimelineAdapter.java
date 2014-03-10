package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.loaders.TimelineListLoader;

public class TimelineAdapter extends LoaderDTOAdapter<TimelineItem, TimelineItemView, TimelineListLoader>
{
    public TimelineAdapter(Context context, LayoutInflater inflater, int timelineLoaderId, int layoutResourceId)
    {
        super(context, inflater, timelineLoaderId, layoutResourceId);
    }

    @Override protected void fineTune(int position, TimelineItem dto, TimelineItemView dtoView)
    {
    }
}
