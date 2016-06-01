package com.ayondo.academy.persistence.leaderboard.position;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.leaderboard.position.LeaderboardMarkUserPositionId;
import com.ayondo.academy.api.leaderboard.position.OwnedLeaderboardPositionId;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache
public class LeaderboardPositionIdCacheRx extends BaseDTOCacheRx<LeaderboardMarkUserPositionId, OwnedLeaderboardPositionId>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 2000;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardPositionIdCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
    }
    //</editor-fold>
}
