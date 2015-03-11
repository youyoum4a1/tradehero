package com.tradehero.th.fragments.leaderboard.main;

import android.support.annotation.NonNull;
import com.tradehero.th.api.leaderboard.def.ConnectedLeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.def.DrillDownLeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;

class LeaderboardCommunityTypeFactory
{
    @NonNull
    public static LeaderboardCommunityType createFrom(@NonNull LeaderboardDefDTO leaderboardDefDTO)
    {
        if (leaderboardDefDTO instanceof DrillDownLeaderboardDefDTO)
        {
            return LeaderboardCommunityType.Exchange;
        }
        return LeaderboardCommunityType.TimeRestricted;
    }
}
