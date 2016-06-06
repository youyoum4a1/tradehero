package com.androidth.general.persistence.leaderboard.position;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.leaderboard.position.LeaderboardMarkUserPositionId;
import com.androidth.general.api.leaderboard.position.OwnedLeaderboardPositionId;
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
