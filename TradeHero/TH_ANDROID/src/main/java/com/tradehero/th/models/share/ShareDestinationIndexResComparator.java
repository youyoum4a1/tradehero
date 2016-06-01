package com.ayondo.academy.models.share;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

public class ShareDestinationIndexResComparator implements Comparator<ShareDestination>
{
    @NonNull private final Resources resources;
    @NonNull private final List<Integer> destinationIds;

    //<editor-fold desc="Constructors">
    @Inject public ShareDestinationIndexResComparator(
            @NonNull Context context,
            @NonNull @ShareDestinationId Set<Integer> destinationIds)
    {
        this.resources = context.getResources();
        this.destinationIds = new ArrayList<>(destinationIds);
    }
    //</editor-fold>

    @Override public int compare(ShareDestination left, ShareDestination right)
    {
        if (left == right)
        {
            return 0;
        }
        return indexOf(left).compareTo(indexOf(right));
    }

    @NonNull protected Integer indexOf(@NonNull ShareDestination shareDestination)
    {
        return this.destinationIds.indexOf(resources.getInteger(shareDestination.getIdResId()));
    }
}
