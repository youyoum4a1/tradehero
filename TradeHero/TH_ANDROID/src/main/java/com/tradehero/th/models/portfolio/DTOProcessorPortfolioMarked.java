package com.tradehero.th.models.portfolio;

import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorPortfolioMarked extends DTOProcessorPortfolioReceived<PortfolioDTO>
{
    @NotNull private final PortfolioCache portfolioCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorPortfolioMarked(
            @NotNull UserBaseKey userBaseKey,
            @NotNull PortfolioCache portfolioCache)
    {
        super(userBaseKey);
        this.portfolioCache = portfolioCache;
    }
    //</editor-fold>

    @Override public PortfolioDTO process(PortfolioDTO value)
    {
        PortfolioDTO processed = super.process(value);
        portfolioCache.put(value.getOwnedPortfolioId(), processed);
        return processed;
    }
}
