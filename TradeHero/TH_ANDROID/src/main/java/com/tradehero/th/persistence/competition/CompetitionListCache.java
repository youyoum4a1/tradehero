package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.CompetitionIdList;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.key.CompetitionId;
import com.tradehero.th.network.service.CompetitionServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 5:04 PM To change this template use File | Settings | File Templates. */
@Singleton public class CompetitionListCache extends StraightDTOCache<ProviderId, CompetitionIdList>
{
    public static final int DEFAULT_MAX_SIZE = 50;

    @Inject protected CompetitionServiceWrapper competitionServiceWrapper;
    @Inject protected CompetitionCache competitionCache;

    //<editor-fold desc="Constructors">
    @Inject public CompetitionListCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected CompetitionIdList fetch(ProviderId key) throws Throwable
    {
        return putInternal(key, competitionServiceWrapper.getCompetitions(key));
    }

    protected CompetitionIdList putInternal(ProviderId key, List<CompetitionDTO> fleshedValues)
    {
        CompetitionIdList competitionIds = null;
        if (fleshedValues != null)
        {
            competitionIds = new CompetitionIdList();
            CompetitionId competitionId;
            for (CompetitionDTO competitionDTO: fleshedValues)
            {
                competitionId = competitionDTO.getCompetitionId();
                competitionIds.add(competitionId);
                competitionCache.put(competitionId, competitionDTO);
            }
            put(key, competitionIds);
        }
        return competitionIds;
    }
}
