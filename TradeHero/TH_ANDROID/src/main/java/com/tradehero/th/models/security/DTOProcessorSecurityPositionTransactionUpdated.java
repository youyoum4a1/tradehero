package com.tradehero.th.models.security;

import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.position.SecurityPositionTransactionDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.position.SecurityPositionDetailCacheRx;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorSecurityPositionTransactionUpdated extends DTOProcessorSecurityPositionTransactionReceived
{
    @NotNull private final PortfolioCacheRx portfolioCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorSecurityPositionTransactionUpdated(
            @NotNull SecurityId securityId,
            @NotNull UserBaseKey ownerId,
            @NotNull SecurityPositionDetailCacheRx securityPositionDetailCache,
            @NotNull PortfolioCacheRx portfolioCache)
    {
        super(securityId, ownerId, securityPositionDetailCache);
        this.portfolioCache = portfolioCache;
    }
    //</editor-fold>

    @Override public SecurityPositionTransactionDTO process(@NotNull SecurityPositionTransactionDTO value)
    {
        value = super.process(value);
        PortfolioDTO portfolioDTO = value.portfolio;
        portfolioCache.onNext(portfolioDTO.getOwnedPortfolioId(), portfolioDTO);
        // Add positions?
        return value;
    }
}
