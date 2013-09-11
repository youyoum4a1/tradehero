package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.api.local.TimelineItemBuilder;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.widget.timeline.TimelineItemView;

public class TimelineAdapter extends DTOAdapter<TimelineItem, TimelineItemView>
{
    public TimelineAdapter(Context context, LayoutInflater inflater, int layoutResourceId, TimelineDTO timelineDTO)
    {
        super(context, inflater, layoutResourceId);

        TimelineItemBuilder timelineBuilder = new TimelineItemBuilder(timelineDTO);
        timelineBuilder.buildFrom(timelineDTO);
        setItems(timelineBuilder.getItems());
    }

    @Override protected View getView(int position, TimelineItemView convertView)
    {
        convertView.display((TimelineItem) getItem(position));
        return convertView;
    }
}
