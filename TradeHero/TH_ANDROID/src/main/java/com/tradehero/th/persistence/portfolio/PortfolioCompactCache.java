package com.tradehero.th.persistence.portfolio;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.network.service.PortfolioService;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class PortfolioCompactCache extends StraightDTOCache<PortfolioId, PortfolioCompactDTO>
{
    public static final String TAG = PortfolioCompactCache.class.getName();
    public static final int DEFAULT_MAX_SIZE = 200;

    @Inject Lazy<PortfolioService> portfolioService;

    //<editor-fold desc="Constructors">
    @Inject public PortfolioCompactCache()
    {
        super(200);
    }
    //</editor-fold>

    protected PortfolioCompactDTO fetch(PortfolioId key) throws Throwable
    {
        throw new IllegalStateException("You cannot fetch an individual PortfolioCompactDTO");
    }

    @Override public PortfolioCompactDTO put(PortfolioId key, PortfolioCompactDTO value)
    {
        THLog.e(TAG, "put " + value, new Exception());

        // HACK We need to take care of the bug https://www.pivotaltracker.com/story/show/61190894
        {
            PortfolioCompactDTO current = get(key);
            if (current != null && current.providerId != null)
            {
                value.providerId = current.providerId;
            }
        }

        return super.put(key, value);    //To change body of overridden methods use File | Settings | File Templates.
    }
}