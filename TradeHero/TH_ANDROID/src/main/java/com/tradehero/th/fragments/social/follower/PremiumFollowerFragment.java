package com.tradehero.th.fragments.social.follower;

import android.content.Context;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.persistence.social.HeroType;
import javax.inject.Inject;

public class PremiumFollowerFragment extends FollowerManagerTabFragment
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    @Override protected HeroType getFollowerType()
    {
        return HeroType.PREMIUM;
    }

    @Override protected void handleFollowerSummaryDTOReceived(FollowerSummaryDTO fromServer)
    {
        display(fromServer.getPaidFollowerSummaryDTO());
    }
}