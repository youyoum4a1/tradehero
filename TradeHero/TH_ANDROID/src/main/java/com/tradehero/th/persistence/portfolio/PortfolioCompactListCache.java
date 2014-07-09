package com.tradehero.th.persistence.portfolio;

import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.PortfolioServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class PortfolioCompactListCache extends StraightCutDTOCacheNew<
        UserBaseKey,
        PortfolioCompactDTOList,
        OwnedPortfolioIdList>
{
    public static final int DEFAULT_MAX_SIZE = 50;

    @NotNull protected final Lazy<PortfolioServiceWrapper> portfolioServiceWrapper;
    @NotNull protected final Lazy<PortfolioCompactCache> portfolioCompactCache;
    @NotNull protected final Lazy<PortfolioCache> portfolioCache;
    @NotNull protected final CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    @Inject public PortfolioCompactListCache(
            @NotNull Lazy<PortfolioServiceWrapper> portfolioServiceWrapper,
            @NotNull Lazy<PortfolioCompactCache> portfolioCompactCache,
            @NotNull Lazy<PortfolioCache> portfolioCache,
            @NotNull CurrentUserId currentUserId)
    {
        super(DEFAULT_MAX_SIZE);
        this.portfolioServiceWrapper = portfolioServiceWrapper;
        this.portfolioCompactCache = portfolioCompactCache;
        this.portfolioCache = portfolioCache;
        this.currentUserId = currentUserId;
    }
    //</editor-fold>

    @Override @NotNull public PortfolioCompactDTOList fetch(@NotNull UserBaseKey key) throws Throwable
    {
        return portfolioServiceWrapper.get().getPortfolios(key, key.equals(currentUserId.toUserBaseKey()));
    }

    @NotNull @Override protected OwnedPortfolioIdList cutValue(@NotNull UserBaseKey key, @NotNull PortfolioCompactDTOList value)
    {
        portfolioCompactCache.get().put(value);
        return new OwnedPortfolioIdList(key, value);
    }

    @Nullable @Override protected PortfolioCompactDTOList inflateValue(@NotNull UserBaseKey key, @Nullable OwnedPortfolioIdList cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        PortfolioCompactDTOList value = portfolioCompactCache.get().get(cutValue, null);
        if (value.hasNullItem())
        {
            return null;
        }
        return value;
    }

    @Override public void invalidate(@NotNull UserBaseKey key)
    {
        @Nullable PortfolioCompactDTOList value = get(key);
        if (value != null)
        {
            for (@NotNull PortfolioCompactDTO portfolioCompactDTO : value)
            {
                portfolioCompactCache.get().invalidate(portfolioCompactDTO.getPortfolioId());
                portfolioCache.get().invalidate(new OwnedPortfolioId(key, portfolioCompactDTO));
            }
        }
    }

    @Nullable public PortfolioCompactDTO getDefaultPortfolio(@NotNull UserBaseKey key)
    {
        PortfolioCompactDTOList list = get(key);
        if (list == null || list.size() == 0)
        {
            return null;
        }
        return list.getDefaultPortfolio();
    }
}
