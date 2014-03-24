package com.tradehero.th.persistence.portfolio;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.PortfolioService;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 5:04 PM To change this template use File | Settings | File Templates. */
@Singleton public class PortfolioCompactListCache extends StraightDTOCache<UserBaseKey, OwnedPortfolioIdList>
{
    public static final String TAG = PortfolioCompactListCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 50;

    @Inject protected Lazy<PortfolioService> portfolioService;
    @Inject protected Lazy<PortfolioCompactCache> portfolioCompactCache;
    @Inject protected Lazy<PortfolioCache> portfolioCache;

    //<editor-fold desc="Constructors">
    @Inject public PortfolioCompactListCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected OwnedPortfolioIdList fetch(UserBaseKey key) throws Throwable
    {
        return putInternal(key, portfolioService.get().getPortfolios(key.key, true));
    }

    protected OwnedPortfolioIdList putInternal(UserBaseKey key, List<PortfolioCompactDTO> fleshedValues)
    {
        OwnedPortfolioIdList ownedPortfolioIds = null;
        if (fleshedValues != null)
        {
            ownedPortfolioIds = new OwnedPortfolioIdList();
            OwnedPortfolioId ownedPortfolioId;
            for (PortfolioCompactDTO portfolioCompactDTO: fleshedValues)
            {
                //THLog.d(TAG, portfolioCompactDTO.toString());
                ownedPortfolioId = new OwnedPortfolioId(key, portfolioCompactDTO.getPortfolioId());
                ownedPortfolioIds.add(ownedPortfolioId);
                portfolioCompactCache.get().put(portfolioCompactDTO.getPortfolioId(), portfolioCompactDTO);
            }
            put(key, ownedPortfolioIds);
        }
        return ownedPortfolioIds;
    }

    @Override public void invalidate(UserBaseKey key)
    {
        OwnedPortfolioIdList value = get(key);
        if (value != null)
        {
            for (OwnedPortfolioId ownedPortfolioId : value)
            {
                portfolioCompactCache.get().invalidate(ownedPortfolioId.getPortfolioId());
                portfolioCache.get().invalidate(ownedPortfolioId);
            }
        }
    }

    /**
     * The default portfolio is the one without providerId.
     * @param key
     * @return
     */
    public OwnedPortfolioId getDefaultPortfolio(UserBaseKey key)
    {
        OwnedPortfolioIdList list = get(key);
        if (list == null || list.size() == 0)
        {
            return null;
        }
        // Find the one without providerId
        PortfolioId portfolioId;
        PortfolioCompactDTO portfolioCompactDTO;
        for (OwnedPortfolioId ownedPortfolioId : list)
        {
            if (ownedPortfolioId == null)
            {
                return null;
            }
            portfolioId = ownedPortfolioId.getPortfolioId();
            if (portfolioId.key == null)
            {
                return null;
            }
            portfolioCompactDTO = portfolioCompactCache.get().get(portfolioId);
            if (portfolioCompactDTO != null && portfolioCompactDTO.providerId == null && !portfolioCompactDTO.isWatchlist)
            {
                return ownedPortfolioId;
            }
        }
        return null;
    }
}
