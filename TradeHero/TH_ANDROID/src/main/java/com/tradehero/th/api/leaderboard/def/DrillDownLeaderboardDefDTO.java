package com.tradehero.th.api.leaderboard.def;

import com.tradehero.th.api.leaderboard.key.DrillDownLeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import org.jetbrains.annotations.NotNull;

public class DrillDownLeaderboardDefDTO extends LeaderboardDefDTO
{
    //<editor-fold desc="Constructors">
    public DrillDownLeaderboardDefDTO()
    {
        super();
    }
    //</editor-fold>

    @NotNull @Override public LeaderboardDefListKey getLeaderboardDefListKey()
    {
        return new DrillDownLeaderboardDefListKey();
    }
}
