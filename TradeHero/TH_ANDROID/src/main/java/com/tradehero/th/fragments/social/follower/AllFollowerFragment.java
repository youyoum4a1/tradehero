package com.tradehero.th.fragments.social.follower;

import android.content.Context;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.persistence.social.HeroType;
import javax.inject.Inject;

public class AllFollowerFragment extends FollowerManagerTabFragment
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    @Override protected HeroType getFollowerType()
    {
        return HeroType.ALL;
    }

    @Override protected void handleFollowerSummaryDTOReceived(FollowerSummaryDTO fromServer)
    {
        display(fromServer);
    }
}
