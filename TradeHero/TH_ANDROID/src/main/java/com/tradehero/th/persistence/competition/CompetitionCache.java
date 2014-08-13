package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.CompetitionDTOList;
import com.tradehero.th.api.competition.key.CompetitionId;
import com.tradehero.th.network.service.CompetitionServiceWrapper;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardUserCache;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class CompetitionCache extends StraightCutDTOCacheNew<CompetitionId, CompetitionDTO, CompetitionCutDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    @NotNull private final CompetitionServiceWrapper competitionServiceWrapper;
    @NotNull private final Lazy<LeaderboardDefCache> leaderboardDefCache;
    @NotNull private final Lazy<LeaderboardUserCache> leaderboardUserCache;

    //<editor-fold desc="Constructors">
    @Inject public CompetitionCache(
            @NotNull CompetitionServiceWrapper competitionServiceWrapper,
            @NotNull Lazy<LeaderboardDefCache> leaderboardDefCache,
            @NotNull Lazy<LeaderboardUserCache> leaderboardUserCache)
    {
        this(DEFAULT_MAX_SIZE, competitionServiceWrapper, leaderboardDefCache, leaderboardUserCache);
    }

    public CompetitionCache(
            int maxSize,
            @NotNull CompetitionServiceWrapper competitionServiceWrapper,
            @NotNull Lazy<LeaderboardDefCache> leaderboardDefCache,
            @NotNull Lazy<LeaderboardUserCache> leaderboardUserCache)
    {
        super(maxSize);
        this.competitionServiceWrapper = competitionServiceWrapper;
        this.leaderboardDefCache = leaderboardDefCache;
        this.leaderboardUserCache = leaderboardUserCache;
    }
    //</editor-fold>

    @Override @NotNull public CompetitionDTO fetch(@NotNull CompetitionId key) throws Throwable
    {
        return competitionServiceWrapper.getCompetition(key);
    }

    @Override @NotNull protected CompetitionCutDTO cutValue(@NotNull CompetitionId key, @NotNull CompetitionDTO value)
    {
        return new CompetitionCutDTO(value, leaderboardDefCache.get(), leaderboardUserCache.get());
    }

    @Nullable @Override protected CompetitionDTO inflateValue(@NotNull CompetitionId key, @Nullable CompetitionCutDTO cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        return cutValue.create(leaderboardDefCache.get(), leaderboardUserCache.get());
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public CompetitionDTOList get(@Nullable List<CompetitionId> competitionIds)
    {
        if (competitionIds == null)
        {
            return null;
        }

        CompetitionDTOList fleshedValues = new CompetitionDTOList();
        for (@NotNull CompetitionId competitionId: competitionIds)
        {
            fleshedValues.add(get(competitionId));
        }
        return fleshedValues;
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public CompetitionDTOList put(@Nullable List<CompetitionDTO> values)
    {
        if (values == null)
        {
            return null;
        }

        CompetitionDTOList previousValues = new CompetitionDTOList();
        for (@NotNull CompetitionDTO competitionDTO: values)
        {
            previousValues.add(put(competitionDTO.getCompetitionId(), competitionDTO));
        }
        return previousValues;
    }
}
