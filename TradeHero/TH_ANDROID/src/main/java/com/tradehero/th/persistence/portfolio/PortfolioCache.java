package com.tradehero.th.persistence.portfolio;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.network.service.PortfolioService;
import dagger.Lazy;
import javax.inject.Inject;
import retrofit.RetrofitError;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 3:28 PM To change this template use File | Settings | File Templates. */
public class PortfolioCache extends StraightDTOCache<String, OwnedPortfolioId, PortfolioDTO>
{
    public static final String TAG = PortfolioCache.class.getName();
    public static final int DEFAULT_MAX_SIZE = 200;

    @Inject Lazy<PortfolioService> portfolioService;
    @Inject Lazy<PortfolioCompactCache> portfolioCompactCache;

    //<editor-fold desc="Constructors">
    public PortfolioCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected PortfolioDTO fetch(OwnedPortfolioId key)
    {
        PortfolioDTO fetched = null;
        try
        {
            fetched = portfolioService.get().getPortfolio(key.userId, key.portfolioId);
        }
        catch (RetrofitError e)
        {
            THLog.e(TAG, "Failed to fetch key " + key, e);
        }
        return fetched;
    }

    @Override public PortfolioDTO put(OwnedPortfolioId key, PortfolioDTO value)
    {
        if (value != null)
        {
            portfolioCompactCache.get().put(key.getPortfolioId(), value);
        }
        return super.put(key, value);
    }
}
