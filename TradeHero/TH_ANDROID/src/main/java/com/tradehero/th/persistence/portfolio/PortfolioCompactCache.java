package com.tradehero.th.persistence.portfolio;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.network.service.PortfolioService;
import dagger.Lazy;
import javax.inject.Inject;

public class PortfolioCompactCache extends StraightDTOCache<Integer, PortfolioId, PortfolioCompactDTO>
{
    public static final String TAG = PortfolioCompactCache.class.getName();
    public static final int DEFAULT_MAX_SIZE = 200;

    @Inject Lazy<PortfolioService> portfolioService;

    //<editor-fold desc="Constructors">
    public PortfolioCompactCache()
    {
        super(200);
    }
    //</editor-fold>

    protected PortfolioCompactDTO fetch(PortfolioId key)
    {
        throw new IllegalStateException("You cannot fetch an individual PortfolioCompactDTO");
    }
}