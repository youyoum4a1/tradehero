package com.tradehero.th.persistence.position;

import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.persistence.competition.ProviderCache;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class SecurityPositionDetailCache extends StraightCutDTOCacheNew<SecurityId, SecurityPositionDetailDTO, SecurityPositionDetailCutDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    // We need to compose here, instead of inheritance, otherwise we get a compile error regarding erasure on put and put.
    @NotNull protected final CurrentUserId currentUserId;
    @NotNull protected final Lazy<SecurityServiceWrapper> securityServiceWrapper;
    @NotNull protected final Lazy<SecurityCompactCache> securityCompactCache;
    @NotNull protected final Lazy<PositionCompactCache> positionCompactCache;
    @NotNull protected final Lazy<PortfolioCache> portfolioCache;
    @NotNull protected final Lazy<ProviderCache> providerCache;

    //<editor-fold desc="Constructors">
    @Inject public SecurityPositionDetailCache(
            @NotNull CurrentUserId currentUserId,
            @NotNull Lazy<SecurityServiceWrapper> securityServiceWrapper,
            @NotNull Lazy<SecurityCompactCache> securityCompactCache,
            @NotNull Lazy<PositionCompactCache> positionCompactCache,
            @NotNull Lazy<PortfolioCache> portfolioCache,
            @NotNull Lazy<ProviderCache> providerCache)
    {
        super(DEFAULT_MAX_SIZE);
        this.currentUserId = currentUserId;
        this.securityServiceWrapper = securityServiceWrapper;
        this.securityCompactCache = securityCompactCache;
        this.positionCompactCache = positionCompactCache;
        this.portfolioCache = portfolioCache;
        this.providerCache = providerCache;
    }
    //</editor-fold>

    @Override @NotNull public SecurityPositionDetailDTO fetch(@NotNull SecurityId key) throws Throwable
    {
        return securityServiceWrapper.get().getSecurity(key);
    }

    @NotNull @Override protected SecurityPositionDetailCutDTO cutValue(@NotNull SecurityId key, @NotNull SecurityPositionDetailDTO value)
    {
        return new SecurityPositionDetailCutDTO(
                value,
                securityCompactCache.get(),
                portfolioCache.get(),
                positionCompactCache.get(),
                providerCache.get(),
                currentUserId.toUserBaseKey());
    }

    @Contract("_, null -> null; _, !null -> !null")
    @Nullable @Override protected SecurityPositionDetailDTO inflateValue(@NotNull SecurityId key, @Nullable SecurityPositionDetailCutDTO cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        return cutValue.create(
                securityCompactCache.get(),
                portfolioCache.get(),
                positionCompactCache.get(),
                providerCache.get(),
                currentUserId.toUserBaseKey());
    }
}
