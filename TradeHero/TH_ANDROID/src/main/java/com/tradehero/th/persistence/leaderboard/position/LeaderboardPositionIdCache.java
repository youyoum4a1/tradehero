package com.tradehero.th.persistence.leaderboard.position;

import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserPositionId;
import com.tradehero.th.api.leaderboard.position.OwnedLeaderboardPositionId;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;

@Singleton @UserCache @Deprecated
public class LeaderboardPositionIdCache extends StraightDTOCacheNew<LeaderboardMarkUserPositionId, OwnedLeaderboardPositionId>
{
    public static final int DEFAULT_MAX_SIZE = 2000;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardPositionIdCache(@NonNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
    }
    //</editor-fold>

    @Override @NonNull public OwnedLeaderboardPositionId fetch(@NonNull LeaderboardMarkUserPositionId key)
    {
        throw new IllegalStateException("You should not fetch for OwnedLeaderboardPositionId");
    }
}
