package com.tradehero.th.models.share;

import android.content.res.Resources;
import com.tradehero.th.R;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ShareDestinationIndexResComparator implements Comparator<ShareDestination>
{
    public static final int ORDERED_SHARE_DESTINATION_IDS = R.array.ordered_share_destinations;

    private Resources resources;
    private List<Integer> destinationIds;

    public ShareDestinationIndexResComparator(Resources resources)
    {
        this.resources = resources;
        this.destinationIds = new ArrayList<>();
        for (int id : resources.getIntArray(ORDERED_SHARE_DESTINATION_IDS))
        {
            if (destinationIds.contains(id))
            {
                throw new IllegalStateException("Destination ids contains twice the id "+ id);
            }
            destinationIds.add(id);
        }
    }

    @Override public int compare(ShareDestination left, ShareDestination right)
    {
        if (left == right)
        {
            return 0;
        }
        return indexOf(left).compareTo(indexOf(right));
    }

    protected Integer indexOf(ShareDestination shareDestination)
    {
        return this.destinationIds.indexOf(resources.getInteger(shareDestination.getIdResId()));
    }
}
