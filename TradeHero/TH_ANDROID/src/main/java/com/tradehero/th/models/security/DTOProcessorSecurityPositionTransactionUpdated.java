package com.tradehero.th.models.security;

import android.support.annotation.NonNull;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.position.SecurityPositionTransactionDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;

public class DTOProcessorSecurityPositionTransactionUpdated extends DTOProcessorSecurityPositionTransactionReceived
{
    @NonNull private final PortfolioCacheRx portfolioCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorSecurityPositionTransactionUpdated(
            @NonNull SecurityId securityId,
            @NonNull UserBaseKey ownerId,
            @NonNull PortfolioCacheRx portfolioCache)
    {
        super(securityId, ownerId);
        this.portfolioCache = portfolioCache;
    }
    //</editor-fold>

    @Override public SecurityPositionTransactionDTO process(@NonNull SecurityPositionTransactionDTO value)
    {
        value = super.process(value);
        PortfolioDTO portfolioDTO = value.portfolio;
        portfolioCache.onNext(portfolioDTO.getOwnedPortfolioId(), portfolioDTO);
        // Add positions?
        return value;
    }
}
