package com.tradehero.th.persistence.position;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.position.PositionCompactId;
import com.tradehero.th.api.position.PositionDTOCompact;
import com.tradehero.th.api.position.PositionDTOCompactList;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.competition.ProviderCache;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// The purpose of this class is to save on memory usage by cutting out the elements that already enjoy their own cache.
class SecurityPositionDetailCutDTO implements DTO
{
    @Nullable public final SecurityId securityId;
    @Nullable public final List<PositionCompactId> positionIds;
    @Nullable public final PortfolioId portfolioId;
    @Nullable public final List<ProviderId> providerIds;
    public final int firstTradeAllTime;

    public SecurityPositionDetailCutDTO(
            @NotNull SecurityPositionDetailDTO securityPositionDetailDTO,
            @NotNull SecurityCompactCache securityCompactCache,
            @NotNull PortfolioCache portfolioCache,
            @NotNull PositionCompactCache positionCompactCache,
            @NotNull ProviderCache providerCache,
            @NotNull UserBaseKey userBaseKey)
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
                            userBaseKey.key,
                            securityPositionDetailDTO.portfolio.id),
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

    @Nullable public SecurityPositionDetailDTO create(
            @NotNull SecurityCompactCache securityCompactCache,
            @NotNull PortfolioCache portfolioCache,
            @NotNull PositionCompactCache positionCompactCache,
            @NotNull ProviderCache providerCache,
            @NotNull UserBaseKey userBaseKey)
    {
        SecurityCompactDTO cachedSecurity = null;
        if (securityId != null)
        {
            cachedSecurity = securityCompactCache.get(securityId);
            if (cachedSecurity == null)
            {
                return null;
            }
        }

        PositionDTOCompactList cachedPositionDTOCompacts = positionCompactCache.get(positionIds);
        if (cachedPositionDTOCompacts != null && cachedPositionDTOCompacts.hasNullItem())
        {
            return null;
        }

        PortfolioDTO cachedPortfolio = null;
        if (portfolioId != null)
        {
            cachedPortfolio = portfolioCache.get(
                    new OwnedPortfolioId(userBaseKey.key, portfolioId.key));
            if (cachedPortfolio == null)
            {
                return null;
            }
        }

        ProviderDTOList cachedProviderDTOs = providerCache.get(providerIds);
        if (cachedProviderDTOs != null && cachedProviderDTOs.hasNullItem())
        {
            return null;
        }

        return new SecurityPositionDetailDTO(
                cachedSecurity,
                cachedPositionDTOCompacts,
                cachedPortfolio,
                cachedProviderDTOs,
                firstTradeAllTime
        );
    }
}
