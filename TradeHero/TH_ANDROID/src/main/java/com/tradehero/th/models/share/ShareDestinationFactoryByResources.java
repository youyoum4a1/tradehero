package com.tradehero.th.models.share;

import android.content.Context;
import android.content.res.Resources;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

public class ShareDestinationFactoryByResources implements ShareDestinationFactory
{
    private final Resources resources;
    private List<Integer> destinationIds;

    @Inject public ShareDestinationFactoryByResources(
            Context context,
            @ShareDestinationId Set<Integer> destinationIds)
    {
        super();
        this.resources = context.getResources();
        this.destinationIds = new ArrayList<>(destinationIds);
    }

    @Override public ArrayList<ShareDestination> getAllShareDestinations()
    {
        ArrayList<ShareDestination> allDestinations = new ArrayList<>();
        addIfListed(allDestinations, new FacebookShareDestination(), destinationIds);
        addIfListed(allDestinations, new LinkedInShareDestination(), destinationIds);
        addIfListed(allDestinations, new TwitterShareDestination(), destinationIds);
        addIfListed(allDestinations, new WeChatShareDestination(), destinationIds);
        addIfListed(allDestinations, new WeiboShareDestination(), destinationIds);
        return allDestinations;
    }

    protected void addIfListed(List<ShareDestination> destinations,
            ShareDestination shareDestination,
            List<Integer> destinationIds)
    {
        if (destinationIds.indexOf(resources.getInteger(shareDestination.getIdResId())) != -1)
        {
            destinations.add(shareDestination);
        }
    }
}
