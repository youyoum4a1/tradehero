package com.tradehero.th.fragments.social.follower;

import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.persistence.social.HeroType;

public class AllFollowerFragment extends FollowerManagerTabFragment
{
    @Override protected HeroType getFollowerType()
    {
        return HeroType.ALL;
    }

    @Override protected void handleFollowerSummaryDTOReceived(FollowerSummaryDTO fromServer)
    {
        display(fromServer);
    }
}
