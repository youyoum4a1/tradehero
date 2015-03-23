package com.tradehero.th.models.share;

import android.content.Context;
import android.content.res.Resources;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ShareDestinationFactoryByResources implements ShareDestinationFactory
{
    @NotNull private final Resources resources;
    @NotNull private final List<Integer> destinationIds;

    //<editor-fold desc="Constructors">
    @Inject public ShareDestinationFactoryByResources(
            @NotNull Context context,
            @NotNull @ShareDestinationId Set<Integer> destinationIds)
    {
        super();
        this.resources = context.getResources();
        this.destinationIds = new ArrayList<>(destinationIds);
    }
    //</editor-fold>

    @Override @NotNull public ArrayList<ShareDestination> getAllShareDestinations()
    {
        ArrayList<ShareDestination> allDestinations = new ArrayList<>();
        addIfListed(allDestinations, new WeChatShareDestination(), destinationIds);
        addIfListed(allDestinations, new WeiboShareDestination(), destinationIds);
        return allDestinations;
    }

    protected void addIfListed(@NotNull List<ShareDestination> destinations,
            @NotNull ShareDestination shareDestination,
            @NotNull List<Integer> destinationIds)
    {
        if (destinationIds.indexOf(resources.getInteger(shareDestination.getIdResId())) != -1)
        {
            destinations.add(shareDestination);
        }
    }
}
