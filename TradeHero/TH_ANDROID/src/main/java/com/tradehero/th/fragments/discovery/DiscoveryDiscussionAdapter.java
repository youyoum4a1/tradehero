package com.tradehero.th.fragments.discovery;

import android.content.Context;
import android.support.annotation.LayoutRes;
import com.tradehero.th.adapters.DTOAdapterNew;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import org.jetbrains.annotations.NotNull;

public class DiscoveryDiscussionAdapter extends DTOAdapterNew<TimelineItemDTOKey>
{
    public DiscoveryDiscussionAdapter(@NotNull Context context, @LayoutRes int layoutResourceId)
    {
        super(context, layoutResourceId);
    }

    public void replaceWith(TimelineItemDTOKey timelineItemDTOKey)
    {
        clear();
    }
}
