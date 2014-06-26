package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.key.CompetitionId;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class CompetitionCache extends StraightCutDTOCacheNew<CompetitionId, CompetitionDTO, CompetitionCutDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    @NotNull private final LeaderboardDefCache leaderboardDefCache;

    //<editor-fold desc="Constructors">
    @Inject public CompetitionCache(
            @NotNull LeaderboardDefCache leaderboardDefCache)
    {
        this(DEFAULT_MAX_SIZE, leaderboardDefCache);
    }

    public CompetitionCache(
            int maxSize,
            @NotNull LeaderboardDefCache leaderboardDefCache)
    {
        super(maxSize);
        this.leaderboardDefCache = leaderboardDefCache;
    }
    //</editor-fold>

    @Override @NotNull public CompetitionDTO fetch(@NotNull CompetitionId key) throws Throwable
    {
        throw new IllegalStateException("There is no fetch on this cache");
    }

    @Override @NotNull protected CompetitionCutDTO cutValue(@NotNull CompetitionId key, @NotNull CompetitionDTO value)
    {
        return new CompetitionCutDTO(value, leaderboardDefCache);
    }

    @Nullable @Override protected CompetitionDTO inflateValue(@NotNull CompetitionId key, @Nullable CompetitionCutDTO cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        return cutValue.create(leaderboardDefCache);
    }

    @Contract("null -> null; !null -> !null") @Nullable
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

    @Contract("null -> null; !null -> !null") @Nullable
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
}
