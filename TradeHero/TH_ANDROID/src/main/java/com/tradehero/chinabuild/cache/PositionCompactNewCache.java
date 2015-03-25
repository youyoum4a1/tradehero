package com.tradehero.chinabuild.cache;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.network.service.CompetitionServiceWrapper;
import dagger.Lazy;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class PositionCompactNewCache extends StraightDTOCacheNew<PositionDTOKey, PositionDTO>
{
    public static final int DEFAULT_MAX_SIZE = 500;
    @NotNull private final Lazy<CompetitionServiceWrapper> competitionServiceWrapper;

    @Inject public PositionCompactNewCache(
            @NotNull Lazy<CompetitionServiceWrapper> competitionServiceWrapper
    )
    {
        this(DEFAULT_MAX_SIZE, competitionServiceWrapper);
    }

    public PositionCompactNewCache(
            int maxSize,
            @NotNull Lazy<CompetitionServiceWrapper> competitionServiceWrapper)
    {
        super(maxSize);
        this.competitionServiceWrapper = competitionServiceWrapper;
    }

    @Override @NotNull public PositionDTO fetch(@NotNull PositionDTOKey key) throws Throwable
    {
        return competitionServiceWrapper.get().getPositionDTO(key);
    }
}
