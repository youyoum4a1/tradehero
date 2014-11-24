package com.tradehero.th.fragments.leaderboard.main;

import android.support.annotation.NonNull;
import com.tradehero.th.api.leaderboard.def.ConnectedLeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.def.DrillDownLeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import javax.inject.Inject;

class LeaderboardCommunityTypeFactory
{
    //<editor-fold desc="Constructors">
    @Inject public LeaderboardCommunityTypeFactory()
    {
        super();
    }
    //</editor-fold>

    @NonNull
    public LeaderboardCommunityType createFrom(@NonNull LeaderboardDefDTO leaderboardDefDTO)
    {
        if (leaderboardDefDTO instanceof ConnectedLeaderboardDefDTO)
        {
            return LeaderboardCommunityType.Connected;
        }
        if (leaderboardDefDTO instanceof DrillDownLeaderboardDefDTO)
        {
            return LeaderboardCommunityType.DrillDown;
        }
        return LeaderboardCommunityType.TimeRestricted;
    }
}
