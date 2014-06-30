package com.tradehero.th.models.share;

import android.content.Context;
import android.content.res.Resources;
import com.tradehero.thm.R;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class ShareDestinationIndexResComparator implements Comparator<ShareDestination>
{
    public static final int ORDERED_SHARE_DESTINATION_IDS = R.array.ordered_share_destinations;

    @NotNull private final Resources resources;
    @NotNull private final List<Integer> destinationIds;

    @Inject public ShareDestinationIndexResComparator(
            @NotNull Context context,
            @NotNull @ShareDestinationId Set<Integer> destinationIds)
    {
        this.resources = context.getResources();
        this.destinationIds = new ArrayList<>(destinationIds);
    }

    @Override public int compare(ShareDestination left, ShareDestination right)
    {
        if (left == right)
        {
            return 0;
        }
        return indexOf(left).compareTo(indexOf(right));
    }

    @NotNull protected Integer indexOf(@NotNull ShareDestination shareDestination)
    {
        return this.destinationIds.indexOf(resources.getInteger(shareDestination.getIdResId()));
    }
}
