package com.tradehero.th.api.leaderboard.def;

import com.tradehero.th.api.leaderboard.key.ConnectedLeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
        return new ConnectedLeaderboardDefListKey();
    }
}
