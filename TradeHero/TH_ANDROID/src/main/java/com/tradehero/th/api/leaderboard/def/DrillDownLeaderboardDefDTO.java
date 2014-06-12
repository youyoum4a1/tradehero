package com.tradehero.th.api.leaderboard.def;

import com.tradehero.th.api.leaderboard.key.DrillDownLeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;

public class DrillDownLeaderboardDefDTO extends LeaderboardDefDTO
{
    //<editor-fold desc="Constructors">
    public DrillDownLeaderboardDefDTO()
    {
        super();
    }
    //</editor-fold>

    @Override public LeaderboardDefListKey getLeaderboardDefListKey()
    {
        return new DrillDownLeaderboardDefListKey();
    }
}
