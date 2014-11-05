package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOUtil;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.network.service.LeaderboardServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Singleton @UserCache
public class LeaderboardCache extends StraightCutDTOCacheNew<LeaderboardKey, LeaderboardDTO, LeaderboardCutDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    // We need to compose here, instead of inheritance, otherwise we get a compile error regarding erasure on put and put.
    @NonNull private final Lazy<LeaderboardUserCache> leaderboardUserCache;
    @NonNull private final LeaderboardUserDTOUtil leaderboardUserDTOUtil;
    @NonNull private final LeaderboardServiceWrapper leaderboardServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardCache(
            @NonNull Lazy<LeaderboardUserCache> leaderboardUserCache,
            @NonNull LeaderboardUserDTOUtil leaderboardUserDTOUtil,
            @NonNull LeaderboardServiceWrapper leaderboardServiceWrapper,
            @NonNull DTOCacheUtilNew dtoCacheUtil)
    {
        this(
                DEFAULT_MAX_SIZE,
                leaderboardUserCache,
                leaderboardUserDTOUtil,
                leaderboardServiceWrapper,
                dtoCacheUtil);
    }

    public LeaderboardCache(
            int maxSize,
            @NonNull Lazy<LeaderboardUserCache> leaderboardUserCache,
            @NonNull LeaderboardUserDTOUtil leaderboardUserDTOUtil,
            @NonNull LeaderboardServiceWrapper leaderboardServiceWrapper,
            @NonNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(maxSize, dtoCacheUtil);
        this.leaderboardUserCache = leaderboardUserCache;
        this.leaderboardUserDTOUtil = leaderboardUserDTOUtil;
        this.leaderboardServiceWrapper = leaderboardServiceWrapper;
    }
    //</editor-fold>

    @Override @NonNull public LeaderboardDTO fetch(@NonNull LeaderboardKey key) throws Throwable
    {
        return leaderboardServiceWrapper.getLeaderboard(key);
    }

    @NonNull @Override protected LeaderboardCutDTO cutValue(
            @NonNull LeaderboardKey key,
            @NonNull LeaderboardDTO value)
    {
        return new LeaderboardCutDTO(
                value,
                leaderboardUserCache.get(),
                leaderboardUserDTOUtil);
    }

    @Nullable @Override protected LeaderboardDTO inflateValue(
            @NonNull LeaderboardKey key,
            @Nullable LeaderboardCutDTO cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        return cutValue.create(leaderboardUserCache.get());
    }
}
