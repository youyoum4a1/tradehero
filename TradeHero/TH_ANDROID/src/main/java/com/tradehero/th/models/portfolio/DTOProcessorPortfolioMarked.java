package com.tradehero.th.models.portfolio;

import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class DTOProcessorPortfolioMarked extends DTOProcessorPortfolioReceived<PortfolioDTO>
{
    @NotNull private final PortfolioCacheRx portfolioCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorPortfolioMarked(
            @NotNull UserBaseKey userBaseKey,
            @NotNull PortfolioCacheRx portfolioCache)
    {
        super(userBaseKey);
        this.portfolioCache = portfolioCache;
    }
    //</editor-fold>

    @Override public PortfolioDTO process(@NotNull PortfolioDTO value)
    {
        PortfolioDTO processed = super.process(value);
        portfolioCache.onNext(value.getOwnedPortfolioId(), processed);
        return processed;
    }
}
