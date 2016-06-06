package com.androidth.general.models.security;

import android.support.annotation.NonNull;
import com.androidth.general.api.portfolio.PortfolioDTO;
import com.androidth.general.api.position.SecurityPositionTransactionDTO;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.persistence.portfolio.PortfolioCacheRx;

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
