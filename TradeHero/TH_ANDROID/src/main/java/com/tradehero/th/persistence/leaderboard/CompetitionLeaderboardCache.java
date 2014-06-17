package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.persistence.PartialDTOCache;
import com.tradehero.common.persistence.THLruCache;
import com.tradehero.th.api.competition.AdDTO;
import com.tradehero.th.api.competition.PrizeDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardId;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.network.service.CompetitionServiceWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class CompetitionLeaderboardCache extends PartialDTOCache<CompetitionLeaderboardId, CompetitionLeaderboardDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    // We need to compose here, instead of inheritance, otherwise we get a compile error regarding erasure on put and put.
    private THLruCache<CompetitionLeaderboardId, CompetitionLeaderboardCutDTO> lruCache;
    @NotNull private final CompetitionServiceWrapper competitionServiceWrapper;
    @NotNull private final LeaderboardCache leaderboardCache;

    //<editor-fold desc="Constructors">
    @Inject public CompetitionLeaderboardCache(
            @NotNull CompetitionServiceWrapper competitionServiceWrapper,
            @NotNull LeaderboardCache leaderboardCache)
    {
        this(DEFAULT_MAX_SIZE, competitionServiceWrapper, leaderboardCache);
    }

    public CompetitionLeaderboardCache(
            int maxSize,
            @NotNull CompetitionServiceWrapper competitionServiceWrapper,
            @NotNull LeaderboardCache leaderboardCache)
    {
        super();
        lruCache = new THLruCache<>(maxSize);
        this.competitionServiceWrapper = competitionServiceWrapper;
        this.leaderboardCache = leaderboardCache;
    }
    //</editor-fold>

    protected CompetitionLeaderboardDTO fetch(@NotNull CompetitionLeaderboardId key) throws Throwable
    {
        return competitionServiceWrapper.getCompetitionLeaderboard(key);
    }

    @Override @Nullable public CompetitionLeaderboardDTO get(@NotNull CompetitionLeaderboardId key)
    {
        CompetitionLeaderboardCutDTO competitionLeaderboardCutDTO = this.lruCache.get(key);
        if (competitionLeaderboardCutDTO == null)
        {
            return null;
        }
        return competitionLeaderboardCutDTO.create(leaderboardCache);
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public List<CompetitionLeaderboardDTO> get(@Nullable List<CompetitionLeaderboardId> competitionLeaderboardIds)
    {
        if (competitionLeaderboardIds == null)
        {
            return null;
        }

        List<CompetitionLeaderboardDTO> fleshedValues = new ArrayList<>();

        for (@NotNull CompetitionLeaderboardId competitionLeaderboardId: competitionLeaderboardIds)
        {
            fleshedValues.add(get(competitionLeaderboardId));
        }

        return fleshedValues;
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public List<CompetitionLeaderboardDTO> getOrFetch(@Nullable List<CompetitionLeaderboardId> competitionLeaderboardIds) throws Throwable
    {
        if (competitionLeaderboardIds == null)
        {
            return null;
        }

        List<CompetitionLeaderboardDTO> fleshedValues = new ArrayList<>();

        for (@NotNull CompetitionLeaderboardId competitionLeaderboardId: competitionLeaderboardIds)
        {
            fleshedValues.add(getOrFetch(competitionLeaderboardId));
        }

        return fleshedValues;
    }

    @Override @Nullable public CompetitionLeaderboardDTO put(
            @NotNull CompetitionLeaderboardId key,
            @NotNull CompetitionLeaderboardDTO value)
    {
        CompetitionLeaderboardDTO previous = null;

        CompetitionLeaderboardCutDTO previousCut = lruCache.put(
                key,
                new CompetitionLeaderboardCutDTO(value, leaderboardCache));

        if (previousCut != null)
        {
            previous = previousCut.create(leaderboardCache);
        }

        return previous;
    }

    @Override public void invalidate(@NotNull CompetitionLeaderboardId key)
    {
        lruCache.remove(key);
    }

    @Override public void invalidateAll()
    {
        lruCache.evictAll();
    }

    // The purpose of this class is to save on memory usage by cutting out the elements that already enjoy their own cache.
    // It is static so as not to keep a link back to the cache instance.
    private static class CompetitionLeaderboardCutDTO
    {
        @Nullable public LeaderboardKey leaderboardKey;
        public List<AdDTO> ads;
        public int adFrequencyRows;
        public int adStartRow;
        public List<PrizeDTO> prizes;

        public CompetitionLeaderboardCutDTO(
                @NotNull CompetitionLeaderboardDTO competitionLeaderboardDTO,
                @NotNull LeaderboardCache leaderboardCache)
        {
            if (competitionLeaderboardDTO.leaderboard != null)
            {
                leaderboardCache.put(competitionLeaderboardDTO.leaderboard.getLeaderboardKey(), competitionLeaderboardDTO.leaderboard);
                this.leaderboardKey = competitionLeaderboardDTO.leaderboard.getLeaderboardKey();
            }
            else
            {
                this.leaderboardKey = null;
            }

            ads = competitionLeaderboardDTO.ads;
            adFrequencyRows = competitionLeaderboardDTO.adFrequencyRows;
            adStartRow = competitionLeaderboardDTO.adStartRow;
            prizes = competitionLeaderboardDTO.prizes;
        }

        public CompetitionLeaderboardDTO create(@NotNull LeaderboardCache leaderboardCache)
        {
            return new CompetitionLeaderboardDTO(
                    leaderboardKey != null ? leaderboardCache.get(leaderboardKey) : null,
                    ads,
                    adFrequencyRows,
                    adStartRow,
                    prizes
            );
        }
    }
}
