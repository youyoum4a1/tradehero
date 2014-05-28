package com.tradehero.th.models.share;

import android.content.Context;
import android.content.res.Resources;
import com.tradehero.th.R;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ShareDestinationFactoryByResources implements ShareDestinationFactory
{
    public static final int ORDERED_SHARE_DESTINATION_IDS = R.array.ordered_share_destinations;

    private final Resources resources;
    private List<Integer> destinationIds;

    @Inject public ShareDestinationFactoryByResources(Context context)
    {
        super();
        this.resources = context.getResources();
        this.destinationIds = getShareDestinationIds();
    }

    @Override public ArrayList<ShareDestination> getAllShareDestinations()
    {
        ArrayList<ShareDestination> allDestinations = new ArrayList<>();
        addIfListed(allDestinations, new FacebookShareDestination(), destinationIds);
        addIfListed(allDestinations, new LinkedInShareDestination(), destinationIds);
        addIfListed(allDestinations, new TwitterShareDestination(), destinationIds);
        addIfListed(allDestinations, new WeChatShareDestination(), destinationIds);
        return allDestinations;
    }

    protected List<Integer> getShareDestinationIds()
    {
        List<Integer> destinationIds = new ArrayList<>();
        for (int id : resources.getIntArray(ORDERED_SHARE_DESTINATION_IDS))
        {
            if (destinationIds.contains(id))
            {
                throw new IllegalStateException("Destination ids contains twice the id "+ id);
            }
            destinationIds.add(id);
        }
        return destinationIds;
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
