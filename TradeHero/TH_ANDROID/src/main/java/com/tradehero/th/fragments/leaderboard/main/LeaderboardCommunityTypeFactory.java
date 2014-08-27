package com.tradehero.th.fragments.leaderboard.main;

import com.tradehero.th.api.leaderboard.def.ConnectedLeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.def.DrillDownLeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

class LeaderboardCommunityTypeFactory
{
    //<editor-fold desc="Constructors">
    @Inject public LeaderboardCommunityTypeFactory()
    {
        super();
    }
    //</editor-fold>

    @NotNull
    public LeaderboardCommunityType createFrom(@NotNull LeaderboardDefDTO leaderboardDefDTO)
    {
        if (leaderboardDefDTO instanceof ConnectedLeaderboardDefDTO)
        {
            return LeaderboardCommunityType.Connected;
        }
        if (leaderboardDefDTO instanceof DrillDownLeaderboardDefDTO)
        {
            return LeaderboardCommunityType.DrillDown;
        }
        if (leaderboardDefDTO.isTimeRestrictedLeaderboard())
        {
            return LeaderboardCommunityType.TimeRestricted;
        }
        return LeaderboardCommunityType.SkillAndCountry;
    }
}
