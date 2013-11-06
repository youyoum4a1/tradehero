package com.tradehero.th.persistence.leaderboard.position;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.leaderboard.position.LeaderbordMarkUserPositionId;
import com.tradehero.th.api.leaderboard.position.OwnedLeaderboardPositionId;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by julien on 1/11/13
 */

@Singleton
public class LeaderboardPositionIdCache extends StraightDTOCache<LeaderbordMarkUserPositionId, OwnedLeaderboardPositionId>
{

    public static final int DEFAULT_MAX_SIZE = 2000;

    @Inject public LeaderboardPositionIdCache()
    {
        super(DEFAULT_MAX_SIZE);
    }

    @Override protected OwnedLeaderboardPositionId fetch(LeaderbordMarkUserPositionId key)
    {
        throw new IllegalStateException("You should not fetch for OwnedLeaderboardPositionId");
    }
}
