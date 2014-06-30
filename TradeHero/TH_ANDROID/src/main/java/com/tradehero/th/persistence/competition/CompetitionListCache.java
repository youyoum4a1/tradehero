package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.CompetitionIdList;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.key.CompetitionId;
import com.tradehero.th.network.service.CompetitionServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class CompetitionListCache extends StraightDTOCacheNew<ProviderId, CompetitionIdList>
{
    public static final int DEFAULT_MAX_SIZE = 50;

    @NotNull private final CompetitionServiceWrapper competitionServiceWrapper;
    @NotNull private final CompetitionCache competitionCache;

    //<editor-fold desc="Constructors">
    @Inject public CompetitionListCache(
            @NotNull CompetitionServiceWrapper competitionServiceWrapper,
            @NotNull CompetitionCache competitionCache)
    {
        super(DEFAULT_MAX_SIZE);
        this.competitionServiceWrapper = competitionServiceWrapper;
        this.competitionCache = competitionCache;
    }
    //</editor-fold>

    @Override @NotNull public CompetitionIdList fetch(@NotNull ProviderId key) throws Throwable
    {
        return putInternal(key, competitionServiceWrapper.getCompetitions(key));
    }

    @NotNull protected CompetitionIdList putInternal(@NotNull ProviderId key, @NotNull List<CompetitionDTO> fleshedValues)
    {
        CompetitionIdList competitionIds = new CompetitionIdList();
        CompetitionId competitionId;
        for (@NotNull CompetitionDTO competitionDTO: fleshedValues)
        {
            competitionId = competitionDTO.getCompetitionId();
            competitionIds.add(competitionId);
            competitionCache.put(competitionId, competitionDTO);
        }
        put(key, competitionIds);
        return competitionIds;
    }
}
