package com.tradehero.th.api.leaderboard.def;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.api.leaderboard.key.ConnectedLeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;

public class ConnectedLeaderboardDefDTO extends LeaderboardDefDTO
{
    @Nullable public Integer bannerResId;

    //<editor-fold desc="Constructors">
    public ConnectedLeaderboardDefDTO()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override public LeaderboardDefListKey getLeaderboardDefListKey()
    {
        return new ConnectedLeaderboardDefListKey(1);
    }
}
