package com.tradehero.th.models.share;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

public class ShareDestinationFactoryByResources implements ShareDestinationFactory
{
    @NonNull private final Resources resources;
    @NonNull private final List<Integer> destinationIds;

    //<editor-fold desc="Constructors">
    @Inject public ShareDestinationFactoryByResources(
            @NonNull Context context,
            @NonNull @ShareDestinationId Set<Integer> destinationIds)
    {
        super();
        this.resources = context.getResources();
        this.destinationIds = new ArrayList<>(destinationIds);
    }
    //</editor-fold>

    @Override @NonNull public ArrayList<ShareDestination> getAllShareDestinations()
    {
        ArrayList<ShareDestination> allDestinations = new ArrayList<>();
        addIfListed(allDestinations, new FacebookShareDestination(), destinationIds);
        //addIfListed(allDestinations, new LinkedInShareDestination(), destinationIds);
        //addIfListed(allDestinations, new TwitterShareDestination(), destinationIds);
        addIfListed(allDestinations, new WeChatShareDestination(), destinationIds);
        addIfListed(allDestinations, new WeiboShareDestination(), destinationIds);
        return allDestinations;
    }

    protected void addIfListed(@NonNull List<ShareDestination> destinations,
            @NonNull ShareDestination shareDestination,
            @NonNull List<Integer> destinationIds)
    {
        if (destinationIds.indexOf(resources.getInteger(shareDestination.getIdResId())) != -1)
        {
            destinations.add(shareDestination);
        }
    }
}
