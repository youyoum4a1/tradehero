package com.tradehero.th.persistence.portfolio;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.BasicRetrofitErrorHandler;
import com.tradehero.th.network.service.PortfolioService;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.RetrofitError;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 5:04 PM To change this template use File | Settings | File Templates. */
@Singleton public class PortfolioCompactListCache extends StraightDTOCache<UserBaseKey, List<OwnedPortfolioId>>
{
    public static final String TAG = PortfolioCompactListCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 100;

    @Inject protected Lazy<PortfolioService> portfolioService;
    @Inject protected Lazy<PortfolioCompactCache> portfolioCompactCache;

    //<editor-fold desc="Constructors">
    @Inject public PortfolioCompactListCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected List<OwnedPortfolioId> fetch(UserBaseKey key)
    {
        THLog.d(TAG, "fetch " + key);
        try
        {
            return putInternal(key, portfolioService.get().getPortfolios(key.key));
        }
        catch (RetrofitError retrofitError)
        {
            BasicRetrofitErrorHandler.handle(retrofitError);
            THLog.e(TAG, "Error requesting key " + key.toString(), retrofitError);
        }
        return null;
    }

    protected List<OwnedPortfolioId> putInternal(UserBaseKey key, List<PortfolioCompactDTO> fleshedValues)
    {
        List<OwnedPortfolioId> ownedPortfolioIds = null;
        if (fleshedValues != null)
        {
            ownedPortfolioIds = new ArrayList<>();
            OwnedPortfolioId ownedPortfolioId;
            for(PortfolioCompactDTO portfolioCompactDTO: fleshedValues)
            {
                ownedPortfolioId = new OwnedPortfolioId(key, portfolioCompactDTO.getPortfolioId());
                ownedPortfolioIds.add(ownedPortfolioId);
                portfolioCompactCache.get().put(portfolioCompactDTO.getPortfolioId(), portfolioCompactDTO);
            }
            put(key, ownedPortfolioIds);
        }
        return ownedPortfolioIds;
    }
}
