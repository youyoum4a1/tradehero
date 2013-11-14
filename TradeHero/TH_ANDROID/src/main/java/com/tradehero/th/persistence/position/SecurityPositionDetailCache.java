package com.tradehero.th.persistence.position;

import android.support.v4.util.LruCache;
import com.tradehero.common.persistence.PartialDTOCache;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.position.PositionCompactId;
import com.tradehero.th.api.position.PositionDTOCompact;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.SecurityService;
import com.tradehero.th.persistence.competition.ProviderCache;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 4:47 PM To change this template use File | Settings | File Templates. */
@Singleton public class SecurityPositionDetailCache extends PartialDTOCache<SecurityId, SecurityPositionDetailDTO>
{
    public static final String TAG = SecurityPositionDetailCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 1000;

    @Inject protected CurrentUserBaseKeyHolder currentUserBaseKeyHolder;
    // We need to compose here, instead of inheritance, otherwise we get a compile error regarding erasure on put and put.
    private LruCache<SecurityId, SecurityPositionDetailCache.SecurityPositionDetailCutDTO> lruCache;
    @Inject protected Lazy<SecurityService> securityService;
    @Inject protected Lazy<SecurityCompactCache> securityCompactCache;
    @Inject protected Lazy<PositionCompactCache> positionCompactCache;
    @Inject protected Lazy<PortfolioCache> portfolioCache;
    @Inject protected Lazy<ProviderCache> providerCache;

    //<editor-fold desc="Constructors">
    @Inject public SecurityPositionDetailCache()
    {
        this(DEFAULT_MAX_SIZE);
    }

    public SecurityPositionDetailCache(int maxSize)
    {
        super();
        lruCache = new LruCache<>(maxSize);
    }
    //</editor-fold>

    protected SecurityPositionDetailDTO fetch(SecurityId key) throws Throwable
    {
        return securityService.get().getSecurity(key.exchange, key.securitySymbol);
    }

    @Override public SecurityPositionDetailDTO get(SecurityId key)
    {
        SecurityPositionDetailCutDTO securityPositionDetailCutDTO = this.lruCache.get(key);
        SecurityCompactDTO securityCompactDTO = securityCompactCache.get().get(key);
        if (securityPositionDetailCutDTO == null || securityCompactDTO == null)
        {
            return null;
        }
        return securityPositionDetailCutDTO.create(securityCompactCache.get(), portfolioCache.get(), positionCompactCache.get(), providerCache.get(),
                currentUserBaseKeyHolder.getCurrentUserBaseKey());
    }

    @Override public SecurityPositionDetailDTO put(SecurityId key, SecurityPositionDetailDTO value)
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
                        currentUserBaseKeyHolder.getCurrentUserBaseKey()));

        if (previousCut != null)
        {
            previous = previousCut.create(securityCompactCache.get(), portfolioCache.get(), positionCompactCache.get(), providerCache.get(),
                    currentUserBaseKeyHolder.getCurrentUserBaseKey());
        }

        return previous;
    }

    @Override public void invalidate(SecurityId key)
    {
        lruCache.remove(key);
    }

    // The purpose of this class is to save on memory usage by cutting out the elements that already enjoy their own cache.
    // It is static so as not to keep a link back to the cache instance.
    private static class SecurityPositionDetailCutDTO
    {
        public SecurityId securityId;
        public List<PositionCompactId> positionIds;
        public PortfolioId portfolioId;
        public List<ProviderId> providerIds;
        public int firstTradeAllTime;

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
