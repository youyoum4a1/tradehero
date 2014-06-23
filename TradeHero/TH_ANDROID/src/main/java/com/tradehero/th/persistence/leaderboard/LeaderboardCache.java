package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.persistence.PartialDTOCache;
import com.tradehero.common.persistence.THLruCache;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOUtil;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardUserId;
import com.tradehero.th.network.service.LeaderboardServiceWrapper;
import dagger.Lazy;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class LeaderboardCache extends PartialDTOCache<LeaderboardKey, LeaderboardDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    // We need to compose here, instead of inheritance, otherwise we get a compile error regarding erasure on put and put.
    @NotNull private final THLruCache<LeaderboardKey, LeaderboardCutDTO> lruCache;
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
        super();
        lruCache = new THLruCache<>(maxSize);
        this.leaderboardUserCache = leaderboardUserCache;
        this.leaderboardUserDTOUtil = leaderboardUserDTOUtil;
        this.leaderboardServiceWrapper = leaderboardServiceWrapper;
    }
    //</editor-fold>

    protected LeaderboardDTO fetch(@NotNull LeaderboardKey key) throws Throwable
    {
        return leaderboardServiceWrapper.getLeaderboard(key);
    }

    @Override public LeaderboardDTO get(LeaderboardKey key)
    {
        LeaderboardCutDTO leaderboardCutDTO = this.lruCache.get(key);
        if (leaderboardCutDTO == null)
        {
            return null;
        }
        LeaderboardDTO leaderboardDTO = leaderboardCutDTO.create(leaderboardUserCache.get());
        if (leaderboardDTO != null && leaderboardDTO.getExpiresInSeconds() <= 0)
        {
            return null;
        }
        return leaderboardDTO;
    }

    @Override public LeaderboardDTO put(LeaderboardKey key, LeaderboardDTO value)
    {
        LeaderboardDTO previous = null;

        LeaderboardCutDTO previousCut = lruCache.put(
                key,
                new LeaderboardCutDTO(
                        value,
                        leaderboardUserCache.get(),
                        leaderboardUserDTOUtil));

        if (previousCut != null)
        {
            previous = previousCut.create(leaderboardUserCache.get());
        }

        return previous;
    }

    @Override public void invalidate(LeaderboardKey key)
    {
        lruCache.remove(key);
    }

    @Override public void invalidateAll()
    {
        lruCache.evictAll();
    }

    // The purpose of this class is to save on memory usage by cutting out the elements that already enjoy their own cache.
    // It is static so as not to keep a link back to the cache instance.
    private static class LeaderboardCutDTO
    {
        public final int id;
        public final String name;
        public final List<LeaderboardUserId> userIds;
        public final int userIsAtPositionZeroBased;
        public final Date markUtc;
        public final int minPositionCount;
        public final double maxSharpeRatioInPeriodVsSP500;
        public final double maxStdDevPositionRoiInPeriod;
        public final double avgStdDevPositionRoiInPeriod;
        @NotNull public final Date expirationDate;

        public LeaderboardCutDTO(
                LeaderboardDTO leaderboardDTO,
                LeaderboardUserCache leaderboardUserCache,
                LeaderboardUserDTOUtil leaderboardUserDTOUtil)
        {
            this.id = leaderboardDTO.id;
            this.name = leaderboardDTO.name;

            leaderboardUserCache.put(leaderboardUserDTOUtil.map(leaderboardDTO.users));
            userIds = leaderboardUserDTOUtil.getIds(leaderboardDTO.users);

            this.userIsAtPositionZeroBased = leaderboardDTO.userIsAtPositionZeroBased;
            this.markUtc = leaderboardDTO.markUtc;
            this.minPositionCount = leaderboardDTO.minPositionCount;
            this.maxSharpeRatioInPeriodVsSP500 = leaderboardDTO.maxSharpeRatioInPeriodVsSP500;
            this.maxStdDevPositionRoiInPeriod = leaderboardDTO.maxStdDevPositionRoiInPeriod;
            this.avgStdDevPositionRoiInPeriod = leaderboardDTO.avgStdDevPositionRoiInPeriod;
            this.expirationDate = leaderboardDTO.expirationDate;
        }

        public LeaderboardDTO create(LeaderboardUserCache leaderboardUserCache)
        {
            return new LeaderboardDTO(
                    id,
                    name,
                    leaderboardUserCache.get(userIds),
                    userIsAtPositionZeroBased,
                    markUtc,
                    minPositionCount,
                    maxSharpeRatioInPeriodVsSP500,
                    maxStdDevPositionRoiInPeriod,
                    avgStdDevPositionRoiInPeriod,
                    expirationDate);
        }
    }
}
