package com.tradehero.th.persistence.leaderboard.position;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.leaderboard.position.LbPositionId;
import com.tradehero.th.api.leaderboard.position.OwnedLbPositionId;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionCompactId;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by julien on 1/11/13
 */

@Singleton
public class LbPositionIdCache extends StraightDTOCache<LbPositionId, OwnedLbPositionId>
{

    public static final int DEFAULT_MAX_SIZE = 2000;

    @Inject public LbPositionIdCache()
    {
        super(DEFAULT_MAX_SIZE);
    }

    @Override protected OwnedLbPositionId fetch(LbPositionId key)
    {
        throw new IllegalStateException("You should not fetch for OwnedPositionId");
    }
}
