package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.PartialDTOCache;
import com.tradehero.common.persistence.THLruCache;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.key.CompetitionId;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOUtil;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class CompetitionCache extends PartialDTOCache<CompetitionId, CompetitionDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    // We need to compose here, instead of inheritance, otherwise we get a compile error regarding erasure on put and put.
    @NotNull private final THLruCache<CompetitionId, CompetitionCache.CompetitionCutDTO> lruCache;
    @NotNull private final LeaderboardDefCache leaderboardDefCache;
    @NotNull private final LeaderboardUserDTOUtil leaderboardUserDTOUtil;

    //<editor-fold desc="Constructors">
    @Inject public CompetitionCache(
            @NotNull LeaderboardDefCache leaderboardDefCache,
            @NotNull LeaderboardUserDTOUtil leaderboardUserDTOUtil)
    {
        this(DEFAULT_MAX_SIZE, leaderboardDefCache, leaderboardUserDTOUtil);
    }

    public CompetitionCache(
            int maxSize,
            @NotNull LeaderboardDefCache leaderboardDefCache,
            @NotNull LeaderboardUserDTOUtil leaderboardUserDTOUtil)
    {
        super();
        lruCache = new THLruCache<>(maxSize);
        this.leaderboardDefCache = leaderboardDefCache;
        this.leaderboardUserDTOUtil = leaderboardUserDTOUtil;
    }
    //</editor-fold>

    protected CompetitionDTO fetch(CompetitionId key) throws Throwable
    {
        throw new IllegalStateException("There is no fetch on this cache");
    }

    @Override @Nullable public CompetitionDTO get(@NotNull CompetitionId key)
    {
        CompetitionCutDTO leaderboardCutDTO = this.lruCache.get(key);
        if (leaderboardCutDTO == null)
        {
            return null;
        }
        return leaderboardCutDTO.create(leaderboardDefCache);
    }

    @Override public @Nullable CompetitionDTO put(@NotNull CompetitionId key, @NotNull CompetitionDTO value)
    {
        CompetitionDTO previous = null;

        CompetitionCutDTO previousCut = lruCache.put(
                key,
                new CompetitionCutDTO(value, leaderboardDefCache));

        if (previousCut != null)
        {
            previous = previousCut.create(leaderboardDefCache);
        }

        return previous;
    }

    @Contract("null -> null; !null -> !null")
    public List<CompetitionDTO> get(@Nullable List<CompetitionId> competitionIds)
    {
        if (competitionIds == null)
        {
            return null;
        }

        List<CompetitionDTO> fleshedValues = new ArrayList<>();

        for (@NotNull CompetitionId competitionId: competitionIds)
        {
            fleshedValues.add(get(competitionId));
        }

        return fleshedValues;
    }

    @Contract("null -> null; !null -> !null")
    public List<CompetitionDTO> put(@Nullable List<CompetitionDTO> values)
    {
        if (values == null)
        {
            return null;
        }

        List<CompetitionDTO> previousValues = new ArrayList<>();

        for (@NotNull CompetitionDTO competitionDTO: values)
        {
            previousValues.add(put(competitionDTO.getCompetitionId(), competitionDTO));
        }

        return previousValues;
    }

    @Override public void invalidate(@NotNull CompetitionId key)
    {
        lruCache.remove(key);
    }

    @Override public void invalidateAll()
    {
        lruCache.evictAll();
    }

    // The purpose of this class is to save on memory usage by cutting out the elements that already enjoy their own cache.
    // It is static so as not to keep a link back to the cache instance.
    private static class CompetitionCutDTO
    {
        public final int id;
        @Nullable public final LeaderboardDefKey leaderboardKey;
        public final String name;
        public final String competitionDurationType;
        public final String iconActiveUrl;
        public final String iconInactiveUrl;
        public final String prizeValueWithCcy;

        public CompetitionCutDTO(
                @NotNull CompetitionDTO competitionDTO,
                @NotNull LeaderboardDefCache leaderboardDefCache)
        {
            this.id = competitionDTO.id;

            if (competitionDTO.leaderboard == null)
            {
                leaderboardKey = null;
            }
            else
            {
                LeaderboardDefKey key = competitionDTO.leaderboard.getLeaderboardDefKey();
                leaderboardDefCache.put(key, competitionDTO.leaderboard);
                leaderboardKey = key;
            }

            this.name = competitionDTO.name;
            this.competitionDurationType = competitionDTO.competitionDurationType;
            this.iconActiveUrl = competitionDTO.iconActiveUrl;
            this.iconInactiveUrl = competitionDTO.iconInactiveUrl;
            this.prizeValueWithCcy = competitionDTO.prizeValueWithCcy;
        }

        public CompetitionDTO create(@NotNull LeaderboardDefCache leaderboardDefCache)
        {
            return new CompetitionDTO(
                    id,
                    leaderboardKey == null ? null : leaderboardDefCache.get(leaderboardKey),
                    name,
                    competitionDurationType,
                    iconActiveUrl,
                    iconInactiveUrl,
                    prizeValueWithCcy
            );
        }
    }
}
