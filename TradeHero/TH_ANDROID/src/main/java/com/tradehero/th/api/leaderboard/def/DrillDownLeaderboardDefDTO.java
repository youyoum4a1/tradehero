package com.tradehero.th.api.leaderboard.def;

import com.tradehero.th.api.leaderboard.key.DrillDownLeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import android.support.annotation.NonNull;

public class DrillDownLeaderboardDefDTO extends LeaderboardDefDTO
{
    //<editor-fold desc="Constructors">
    public DrillDownLeaderboardDefDTO()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override public LeaderboardDefListKey getLeaderboardDefListKey()
    {
        return new DrillDownLeaderboardDefListKey();
    }
}
