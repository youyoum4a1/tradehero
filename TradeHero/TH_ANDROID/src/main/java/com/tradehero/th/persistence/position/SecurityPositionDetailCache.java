package com.tradehero.th.persistence.position;

import com.tradehero.common.persistence.PartialDTOCache;
import com.tradehero.common.persistence.THLruCache;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.position.PositionCompactId;
import com.tradehero.th.api.position.PositionDTOCompact;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.persistence.competition.ProviderCache;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class SecurityPositionDetailCache extends PartialDTOCache<SecurityId, SecurityPositionDetailDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    // We need to compose here, instead of inheritance, otherwise we get a compile error regarding erasure on put and put.
    @NotNull private final THLruCache<SecurityId, SecurityPositionDetailCutDTO> lruCache;
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
        super();
        lruCache = new THLruCache<>(DEFAULT_MAX_SIZE);
        this.currentUserId = currentUserId;
        this.securityServiceWrapper = securityServiceWrapper;
        this.securityCompactCache = securityCompactCache;
        this.positionCompactCache = positionCompactCache;
        this.portfolioCache = portfolioCache;
        this.providerCache = providerCache;
    }
    //</editor-fold>

    protected SecurityPositionDetailDTO fetch(@NotNull SecurityId key) throws Throwable
    {
        return securityServiceWrapper.get().getSecurity(key);
    }

    @Override public SecurityPositionDetailDTO get(@NotNull SecurityId key)
    {
        SecurityPositionDetailCutDTO securityPositionDetailCutDTO = this.lruCache.get(key);
        SecurityCompactDTO securityCompactDTO = securityCompactCache.get().get(key);
        if (securityPositionDetailCutDTO == null || securityCompactDTO == null)
        {
            return null;
        }
        return securityPositionDetailCutDTO.create(securityCompactCache.get(), portfolioCache.get(), positionCompactCache.get(), providerCache.get(),
                currentUserId.toUserBaseKey());
    }

    @Override public SecurityPositionDetailDTO put(
            @NotNull SecurityId key,
            @NotNull SecurityPositionDetailDTO value)
    {
        SecurityPositionDetailDTO previous = null;

        SecurityPositionDetailCutDTO previousCut = lruCache.put(
                key,
                new SecurityPositionDetailCutDTO(
                        value,
                        securityCompactCache.get(),
                        portfolioCache.get(),
                        positionCompactCache.get(),
                        providerCache.get(),
                        currentUserId.toUserBaseKey()));

        if (previousCut != null)
        {
            previous = previousCut.create(securityCompactCache.get(), portfolioCache.get(), positionCompactCache.get(), providerCache.get(),
                    currentUserId.toUserBaseKey());
        }

        return previous;
    }

    @Override public void invalidate(@NotNull SecurityId key)
    {
        lruCache.remove(key);
    }

    @Override public void invalidateAll()
    {
        lruCache.evictAll();
    }

    // The purpose of this class is to save on memory usage by cutting out the elements that already enjoy their own cache.
    // It is static so as not to keep a link back to the cache instance.
    private static class SecurityPositionDetailCutDTO
    {
        @Nullable public final SecurityId securityId;
        public final List<PositionCompactId> positionIds;
        @Nullable public final PortfolioId portfolioId;
        public final List<ProviderId> providerIds;
        public final int firstTradeAllTime;

        public SecurityPositionDetailCutDTO(
                SecurityPositionDetailDTO securityPositionDetailDTO,
                SecurityCompactCache securityCompactCache,
                PortfolioCache portfolioCache,
                PositionCompactCache positionCompactCache,
                ProviderCache providerCache,
                UserBaseKey userBaseKey)
        {
            if (securityPositionDetailDTO.security != null)
            {
                securityCompactCache.put(securityPositionDetailDTO.getSecurityId(), securityPositionDetailDTO.security);
                this.securityId = securityPositionDetailDTO.getSecurityId();
            }
            else
            {
                this.securityId = null;
            }

            positionCompactCache.put(securityPositionDetailDTO.positions);
            this.positionIds = PositionDTOCompact.getPositionCompactIds(securityPositionDetailDTO.positions);

            if (securityPositionDetailDTO.portfolio != null)
            {
                portfolioCache.put(
                        new OwnedPortfolioId(
                                userBaseKey,
                                securityPositionDetailDTO.portfolio.getPortfolioId()),
                        securityPositionDetailDTO.portfolio);
                this.portfolioId = securityPositionDetailDTO.portfolio.getPortfolioId();
            }
            else
            {
                this.portfolioId = null;
            }

            providerCache.put(securityPositionDetailDTO.providers);
            this.providerIds = ProviderDTO.getProviderIds(securityPositionDetailDTO.providers);

            this.firstTradeAllTime = securityPositionDetailDTO.firstTradeAllTime;
        }

        public SecurityPositionDetailDTO create(
                SecurityCompactCache securityCompactCache,
                PortfolioCache portfolioCache,
                PositionCompactCache positionCompactCache,
                ProviderCache providerCache,
                UserBaseKey userBaseKey)
        {
            return new SecurityPositionDetailDTO(
                    securityId != null ? securityCompactCache.get(securityId) : null,
                    positionCompactCache.get(positionIds),
                    this.portfolioId != null ?
                            portfolioCache.get(new OwnedPortfolioId(
                                userBaseKey,
                                portfolioId))
                            : null,
                    providerCache.get(providerIds),
                    firstTradeAllTime
            );
        }
    }
}
