package com.tradehero.th.fragments.social.follower;

import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.persistence.social.HeroType;

public class FreeFollowerFragment extends FollowerManagerTabFragment
{
    @Override protected HeroType getFollowerType()
    {
        return HeroType.FREE;
    }

    @Override protected void handleFollowerSummaryDTOReceived(FollowerSummaryDTO fromServer)
    {
        display(fromServer.getFreeFollowerSummaryDTO());
    }
}