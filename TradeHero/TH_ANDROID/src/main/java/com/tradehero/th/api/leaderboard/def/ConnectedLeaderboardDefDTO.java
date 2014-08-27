package com.tradehero.th.api.leaderboard.def;

import com.tradehero.th.api.leaderboard.key.ConnectedLeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConnectedLeaderboardDefDTO extends LeaderboardDefDTO
{
    @Nullable public Integer bannerResId;

    //<editor-fold desc="Constructors">
    public ConnectedLeaderboardDefDTO()
    {
        super();
    }
    //</editor-fold>

    @NotNull @Override public LeaderboardDefListKey getLeaderboardDefListKey()
    {
        return new ConnectedLeaderboardDefListKey();
    }
}
