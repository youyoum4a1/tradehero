package com.tradehero.th.models.portfolio;

import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import android.support.annotation.NonNull;

@Deprecated
public class DTOProcessorPortfolioMarked extends DTOProcessorPortfolioReceived<PortfolioDTO>
{
    @NonNull private final PortfolioCacheRx portfolioCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorPortfolioMarked(
            @NonNull UserBaseKey userBaseKey,
            @NonNull PortfolioCacheRx portfolioCache)
    {
        super(userBaseKey);
        this.portfolioCache = portfolioCache;
    }
    //</editor-fold>

    @Override public PortfolioDTO process(@NonNull PortfolioDTO value)
    {
        PortfolioDTO processed = super.process(value);
        portfolioCache.onNext(value.getOwnedPortfolioId(), processed);
        return processed;
    }
}
