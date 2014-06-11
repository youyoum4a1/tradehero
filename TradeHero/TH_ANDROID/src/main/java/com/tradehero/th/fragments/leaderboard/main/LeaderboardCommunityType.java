package com.tradehero.th.fragments.leaderboard.main;

import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import org.jetbrains.annotations.Nullable;

public enum LeaderboardCommunityType
{
    Competition(null),
    Connected(LeaderboardDefListKey.getConnected()),
    SkillAndCountry(LeaderboardDefListKey.getMostSkilled()),
    TimeRestricted(LeaderboardDefListKey.getTimePeriod()),
    DrillDown(LeaderboardDefListKey.getDrillDown());

    @Nullable private final LeaderboardDefListKey key;

    LeaderboardCommunityType(@Nullable LeaderboardDefListKey leaderboardDefListKey)
    {
        this.key = leaderboardDefListKey;
    }

    @Nullable public LeaderboardDefListKey getKey()
    {
        return key;
    }
}
