package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.adapters.ViewDTOSetAdapter;
import com.tradehero.th.api.pagination.RangeDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.utils.Constants;

public class SubTimelineAdapterNew extends ViewDTOSetAdapter<TimelineItemDTO, TimelineItemViewLinear>
{
    @LayoutRes final int layoutResourceId;

    //<editor-fold desc="Constructors">
    public SubTimelineAdapterNew(@NonNull Context context,
            @LayoutRes int layoutResourceId)
    {
        super(context);
        this.layoutResourceId = layoutResourceId;
    }
    //</editor-fold>

    @Override @LayoutRes protected int getViewResId(int position)
    {
        return layoutResourceId;
    }

    @NonNull public RangeDTO getLatestRange()
    {
        TimelineItemDTO latest = getLatest();
        if (latest == null)
        {
            return new RangeDTO(Constants.TIMELINE_ITEM_PER_PAGE, null, null);
        }
        return new RangeDTO(Constants.TIMELINE_ITEM_PER_PAGE, null, latest.id);
    }

    @Nullable public TimelineItemDTO getLatest()
    {
        if (getCount() > 0)
        {
            return getItem(0);
        }
        return null;
    }

    @NonNull public RangeDTO getOlderRange()
    {
        TimelineItemDTO older = getOldest();
        if (older == null)
        {
            return new RangeDTO(Constants.TIMELINE_ITEM_PER_PAGE, null, null);
        }
        return new RangeDTO(Constants.TIMELINE_ITEM_PER_PAGE, older.id, null);
    }

    @Nullable public TimelineItemDTO getOldest()
    {
        if (getCount() > 0)
        {
            return getItem(getCount() - 1);
        }
        return null;
    }
}
