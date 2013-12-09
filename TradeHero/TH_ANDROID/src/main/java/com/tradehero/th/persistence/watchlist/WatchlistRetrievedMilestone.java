package com.tradehero.th.persistence.watchlist;

import com.tradehero.common.persistence.DTORetrievedMilestone;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: tho on 12/5/13.
 */
public class WatchlistRetrievedMilestone extends DTORetrievedMilestone<UserBaseKey, WatchlistPositionDTO, WatchlistPositionCache>
{
    @Inject protected Lazy<WatchlistPositionCache> watchlistPositionCache;

    @Inject public WatchlistRetrievedMilestone(CurrentUserBaseKeyHolder currentUserBaseKeyHolder)
    {
        this(currentUserBaseKeyHolder.getCurrentUserBaseKey());
    }

    public WatchlistRetrievedMilestone(UserBaseKey key)
    {
        super(key);
        DaggerUtils.inject(this);
    }

    @Override
    protected WatchlistPositionCache getCache()
    {
        return watchlistPositionCache.get();
    }

    @Override
    public void launch()
    {
        launchOwn();
    }
}
