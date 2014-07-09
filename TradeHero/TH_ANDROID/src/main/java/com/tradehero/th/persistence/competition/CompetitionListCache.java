package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.th.api.competition.CompetitionDTOList;
import com.tradehero.th.api.competition.CompetitionIdList;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.network.service.CompetitionServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class CompetitionListCache extends StraightCutDTOCacheNew<ProviderId, CompetitionDTOList, CompetitionIdList>
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

    @Override @NotNull public CompetitionDTOList fetch(@NotNull ProviderId key) throws Throwable
    {
        return competitionServiceWrapper.getCompetitions(key);
    }

    @NotNull @Override protected CompetitionIdList cutValue(@NotNull ProviderId key, @NotNull CompetitionDTOList value)
    {
        competitionCache.put(value);
        return value.createKeys();
    }

    @Nullable @Override protected CompetitionDTOList inflateValue(@NotNull ProviderId key, @Nullable CompetitionIdList cutValue)
    {
        CompetitionDTOList value = competitionCache.get(cutValue);
        if (value != null && value.hasNullItem())
        {
            return null;
        }
        return value;
    }
}
