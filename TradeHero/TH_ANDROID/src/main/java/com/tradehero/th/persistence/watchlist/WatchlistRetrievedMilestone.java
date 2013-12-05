package com.tradehero.th.persistence.watchlist;

import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.persistence.DTOKey;
import com.tradehero.common.persistence.DTORetrievedMilestone;
import dagger.Lazy;
import javax.inject.Inject;

/**
 * Created by tho on 12/5/13.
 */
public class WatchlistRetrievedMilestone extends DTORetrievedMilestone
{
    @Inject protected Lazy<WatchlistPositionCache> watchlistPositionCache;

    public WatchlistRetrievedMilestone(DTOKey key)
    {
        super(key);
    }

    @Override
    protected DTOCache getCache()
    {
        return watchlistPositionCache.get();
    }

    @Override
    public void launch()
    {
        launchOwn();
    }
}
