package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.RoundedShapeTransformation;
import com.tradehero.th.R;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.api.local.TimelineItemBuilder;
import com.tradehero.th.api.misc.MediaDTO;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTOEnhanced;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.application.App;
import com.tradehero.th.base.THUser;
import com.tradehero.th.widget.timeline.TimelineItemView;
import java.util.List;

public class UserTimelineAdapter extends BaseAdapter
{
    private final List<TimelineItem> timelineItems;
    private final Context context;

    public UserTimelineAdapter(Context context, TimelineDTO timelineDTO)
    {
        this.context = context;

        TimelineItemBuilder timelineBuilder = new TimelineItemBuilder(timelineDTO);
        timelineBuilder.buildFrom(timelineDTO);
        timelineItems = timelineBuilder.getItems();
    }

    @Override
    public int getCount()
    {
        return timelineItems.size();
    }

    @Override
    public Object getItem(int position)
    {
        return timelineItems.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.user_profile_timeline_item, null);
        }
        TimelineItemView itemView = (TimelineItemView) convertView;
        itemView.display((TimelineItem) getItem(position));
        return itemView;
    }
}
