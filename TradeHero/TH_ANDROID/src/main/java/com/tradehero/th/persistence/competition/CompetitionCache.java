package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.CompetitionId;
import com.tradehero.th.network.service.CompetitionServiceWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 4:40 PM To change this template use File | Settings | File Templates. */
@Singleton public class CompetitionCache extends StraightDTOCache<CompetitionId, CompetitionDTO>
{
    public static final String TAG = CompetitionCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 1000;

    //<editor-fold desc="Constructors">
    @Inject public CompetitionCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected CompetitionDTO fetch(CompetitionId key) throws Throwable
    {
        throw new RuntimeException();
    }

    public List<CompetitionDTO> getOrFetch(List<CompetitionId> providerIds) throws Throwable
    {
        if (providerIds == null)
        {
            return null;
        }

        List<CompetitionDTO> providerDTOList = new ArrayList<>();
        for (CompetitionId providerId : providerIds)
        {
            providerDTOList.add(getOrFetch(providerId, false));
        }
        return providerDTOList;
    }

    public List<CompetitionDTO> get(List<CompetitionId> providerIds)
    {
        if (providerIds == null)
        {
            return null;
        }

        List<CompetitionDTO> fleshedValues = new ArrayList<>();

        for (CompetitionId providerId: providerIds)
        {
            fleshedValues.add(get(providerId));
        }

        return fleshedValues;
    }

    public List<CompetitionDTO> put(List<CompetitionDTO> values)
    {
        if (values == null)
        {
            return null;
        }

        List<CompetitionDTO> previousValues = new ArrayList<>();

        for (CompetitionDTO providerDTO: values)
        {
            previousValues.add(put(providerDTO.getCompetitionId(), providerDTO));
        }

        return previousValues;
    }
}
