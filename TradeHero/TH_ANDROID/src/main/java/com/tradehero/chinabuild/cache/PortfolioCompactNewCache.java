package com.tradehero.chinabuild.cache;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.network.service.PortfolioServiceWrapper;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class PortfolioCompactNewCache extends StraightDTOCacheNew<PortfolioId, PortfolioCompactDTO>
{
    public static final int DEFAULT_MAX_SIZE = 500;
    @NotNull protected final Lazy<PortfolioServiceWrapper> portfolioServiceWrapper;
    @NotNull protected final Lazy<PortfolioCompactCache> portfolioCompactCache;

    @Inject public PortfolioCompactNewCache(
            @NotNull Lazy<PortfolioServiceWrapper> portfolioServiceWrapper,
            @NotNull Lazy<PortfolioCompactCache> portfolioCompactCache)
    {
        this(DEFAULT_MAX_SIZE, portfolioServiceWrapper, portfolioCompactCache);
    }

    public PortfolioCompactNewCache(
            int maxSize,
            @NotNull Lazy<PortfolioServiceWrapper> portfolioServiceWrapper, @NotNull Lazy<PortfolioCompactCache> portfolioCompactCache)
    {
        super(maxSize);
        this.portfolioServiceWrapper = portfolioServiceWrapper;
        this.portfolioCompactCache = portfolioCompactCache;
    }
    //</editor-fold>

    @Override @NotNull public PortfolioCompactDTO fetch(@NotNull PortfolioId key) throws Throwable
    {
        return portfolioServiceWrapper.get().getPortfolioCompact(key);
    }

    @Override public PortfolioCompactDTO put(@NotNull PortfolioId key, @NotNull PortfolioCompactDTO dto)
    {
        portfolioCompactCache.get().invalidate(dto.getPortfolioId());
        portfolioCompactCache.get().put(dto.getPortfolioId(), dto);

        return dto;
    }
}
