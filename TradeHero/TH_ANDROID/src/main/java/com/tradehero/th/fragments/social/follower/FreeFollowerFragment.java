package com.tradehero.th.fragments.social.follower;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.persistence.social.HeroType;
import java.util.List;
import javax.inject.Inject;

public class FreeFollowerFragment extends FollowerManagerTabFragment
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    @NonNull @Override protected HeroType getFollowerType()
    {
        return HeroType.FREE;
    }

    @Nullable @Override protected List<UserFollowerDTO> getFollowers(@NonNull FollowerSummaryDTO fromServer)
    {
        return fromServer.getFreeFollowers();
    }
}