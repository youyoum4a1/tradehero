package com.tradehero.th.persistence.leaderboard.position;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserPositionId;
import com.tradehero.th.api.leaderboard.position.OwnedLeaderboardPositionId;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton
public class LeaderboardPositionIdCache extends StraightDTOCacheNew<LeaderboardMarkUserPositionId, OwnedLeaderboardPositionId>
{
    public static final int DEFAULT_MAX_SIZE = 2000;

    @Inject public LeaderboardPositionIdCache()
    {
        super(DEFAULT_MAX_SIZE);
    }

    @Override public OwnedLeaderboardPositionId fetch(@NotNull LeaderboardMarkUserPositionId key)
    {
        throw new IllegalStateException("You should not fetch for OwnedLeaderboardPositionId");
    }
}
