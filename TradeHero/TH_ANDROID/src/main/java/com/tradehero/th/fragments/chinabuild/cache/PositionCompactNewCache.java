package com.tradehero.th.fragments.chinabuild.cache;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.position.PositionDTOCompact;
import com.tradehero.th.network.service.CompetitionServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class PositionCompactNewCache extends StraightDTOCacheNew<PositionDTOKey, PositionDTOCompact>
{
    public static final int DEFAULT_MAX_SIZE = 500;
    @NotNull private final Lazy<CompetitionServiceWrapper> competitionServiceWrapper;
    //@NotNull protected final Lazy<PortfolioCompactCache> portfolioCompactCache;

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
        //this.portfolioCompactCache = portfolioCompactCache;
    }
    //</editor-fold>

    @Override @NotNull public PositionDTOCompact fetch(@NotNull PositionDTOKey key) throws Throwable
    {
        return competitionServiceWrapper.get().getPositionCompactDTO(key);
    }
    //
    //@Override public PortfolioCompactDTO put(@NotNull PositionDTOKey key, @NotNull PositionDTOCompact dto)
    //{
    //    portfolioCompactCache.get().invalidate(dto.getPortfolioId());
    //    portfolioCompactCache.get().put(dto.getPortfolioId(), dto);
    //
    //    return dto;
    //}
}
