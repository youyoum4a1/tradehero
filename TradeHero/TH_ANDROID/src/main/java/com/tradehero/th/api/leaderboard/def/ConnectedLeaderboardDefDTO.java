package com.tradehero.th.api.leaderboard.def;

import com.tradehero.th.api.leaderboard.key.ConnectedLeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;

public class ConnectedLeaderboardDefDTO extends LeaderboardDefDTO
{
    //<editor-fold desc="Constructors">
    public ConnectedLeaderboardDefDTO()
    {
        super();
    }
    //</editor-fold>

    @Override public LeaderboardDefListKey getLeaderboardDefListKey()
    {
        return new ConnectedLeaderboardDefListKey();
    }
}
