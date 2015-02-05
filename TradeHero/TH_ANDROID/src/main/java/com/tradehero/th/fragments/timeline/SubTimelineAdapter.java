package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.loaders.TimelineListLoader;

public class SubTimelineAdapter extends LoaderDTOAdapter<TimelineItemDTO, TimelineItemViewLinear, TimelineListLoader>
{
    //<editor-fold desc="Constructors">
    public SubTimelineAdapter(@NonNull Context context, int timelineLoaderId, @LayoutRes int layoutResourceId)
    {
        super(context, timelineLoaderId, layoutResourceId);
    }
    //</editor-fold>

    @Override public int getItemViewType(int position)
    {
        return MainTimelineAdapter.TIMELINE_ITEM_TYPE;
    }
}
