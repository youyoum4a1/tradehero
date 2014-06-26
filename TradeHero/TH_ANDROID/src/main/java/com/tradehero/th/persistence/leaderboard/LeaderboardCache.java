package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOUtil;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.network.service.LeaderboardServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class LeaderboardCache extends StraightCutDTOCacheNew<LeaderboardKey, LeaderboardDTO, LeaderboardCutDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    // We need to compose here, instead of inheritance, otherwise we get a compile error regarding erasure on put and put.
    @NotNull private final Lazy<LeaderboardUserCache> leaderboardUserCache;
    @NotNull private final LeaderboardUserDTOUtil leaderboardUserDTOUtil;
    @NotNull private final LeaderboardServiceWrapper leaderboardServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardCache(
            @NotNull Lazy<LeaderboardUserCache> leaderboardUserCache,
            @NotNull LeaderboardUserDTOUtil leaderboardUserDTOUtil,
            @NotNull LeaderboardServiceWrapper leaderboardServiceWrapper)
    {
        this(
                DEFAULT_MAX_SIZE,
                leaderboardUserCache,
                leaderboardUserDTOUtil,
                leaderboardServiceWrapper);
    }

    public LeaderboardCache(
            int maxSize,
            @NotNull Lazy<LeaderboardUserCache> leaderboardUserCache,
            @NotNull LeaderboardUserDTOUtil leaderboardUserDTOUtil,
            @NotNull LeaderboardServiceWrapper leaderboardServiceWrapper)
    {
        super(maxSize);
        this.leaderboardUserCache = leaderboardUserCache;
        this.leaderboardUserDTOUtil = leaderboardUserDTOUtil;
        this.leaderboardServiceWrapper = leaderboardServiceWrapper;
    }
    //</editor-fold>

    @Override public LeaderboardDTO fetch(@NotNull LeaderboardKey key) throws Throwable
    {
        return leaderboardServiceWrapper.getLeaderboard(key);
    }

    @NotNull @Override protected LeaderboardCutDTO cutValue(
            @NotNull LeaderboardKey key,
            @NotNull LeaderboardDTO value)
    {
        return new LeaderboardCutDTO(
                value,
                leaderboardUserCache.get(),
                leaderboardUserDTOUtil);
    }

    @Nullable @Override protected LeaderboardDTO inflateValue(
            @NotNull LeaderboardKey key,
            @Nullable LeaderboardCutDTO cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        return cutValue.create(leaderboardUserCache.get());
    }
}
