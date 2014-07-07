package com.tradehero.th.persistence.portfolio;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.network.service.PortfolioServiceWrapper;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class PortfolioCache extends StraightDTOCacheNew<OwnedPortfolioId, PortfolioDTO>
{
    public static final int DEFAULT_MAX_SIZE = 200;

    @NotNull protected final Lazy<PortfolioServiceWrapper> portfolioServiceWrapper;
    @NotNull protected final Lazy<PortfolioCompactCache> portfolioCompactCache;
    @NotNull protected final PortfolioCompactListCache portfolioCompactListCache;
    @NotNull protected final Lazy<UserProfileCache> userProfileCache;
    @NotNull protected final Lazy<GetPositionsCache> getPositionsCache;

    //<editor-fold desc="Constructors">
    @Inject public PortfolioCache(
            @NotNull Lazy<PortfolioServiceWrapper> portfolioServiceWrapper,
            @NotNull Lazy<PortfolioCompactCache> portfolioCompactCache,
            @NotNull PortfolioCompactListCache portfolioCompactListCache,
            @NotNull Lazy<UserProfileCache> userProfileCache,
            @NotNull Lazy<GetPositionsCache> getPositionsCache)
    {
        super(DEFAULT_MAX_SIZE);
        this.portfolioServiceWrapper = portfolioServiceWrapper;
        this.portfolioCompactCache = portfolioCompactCache;
        this.portfolioCompactListCache = portfolioCompactListCache;
        this.userProfileCache = userProfileCache;
        this.getPositionsCache = getPositionsCache;
    }
    //</editor-fold>

    @Override @NotNull public PortfolioDTO fetch(@NotNull OwnedPortfolioId key) throws Throwable
    {
        return portfolioServiceWrapper.get().getPortfolio(key);
    }

    @Nullable
    @Override public PortfolioDTO put(@NotNull OwnedPortfolioId key, @NotNull PortfolioDTO value)
    {
        portfolioCompactCache.get().put(key.getPortfolioIdKey(), value);
        getPositionsCache.get().invalidate(key);
        return super.put(key, value);
    }

    @Override public void invalidate(@NotNull OwnedPortfolioId key)
    {
        super.invalidate(key);
        getPositionsCache.get().invalidate(key);
        portfolioCompactListCache.getOrFetchAsync(key.getUserBaseKey(), true);
    }
}
