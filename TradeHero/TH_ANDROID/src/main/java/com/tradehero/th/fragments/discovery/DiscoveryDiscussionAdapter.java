package com.tradehero.th.fragments.discovery;

import android.content.Context;
import android.support.annotation.LayoutRes;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.fragments.timeline.TimelineItemViewLinear;
import org.jetbrains.annotations.NotNull;

public class DiscoveryDiscussionAdapter extends ArrayDTOAdapter<TimelineItemDTOKey, TimelineItemViewLinear>
{
    public DiscoveryDiscussionAdapter(@NotNull Context context, @LayoutRes int layoutResourceId)
    {
        super(context, layoutResourceId);
    }

    @Override protected void fineTune(int position, TimelineItemDTOKey dto, TimelineItemViewLinear dtoView)
    {
    }
}
