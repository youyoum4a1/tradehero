package com.tradehero.th.models.share;

import java.util.ArrayList;
import javax.inject.Inject;

public class ShareDestinationFactory
{
    @Inject public ShareDestinationFactory()
    {
        super();
    }

    public ArrayList<ShareDestination> getAllShareDestinations()
    {
        ArrayList<ShareDestination> allDestinations = new ArrayList<>();
        allDestinations.add(new FacebookShareDestination());
        allDestinations.add(new LinkedInShareDestination());
        allDestinations.add(new TwitterShareDestination());
        allDestinations.add(new WeChatShareDestination());
        return allDestinations;
    }
}
