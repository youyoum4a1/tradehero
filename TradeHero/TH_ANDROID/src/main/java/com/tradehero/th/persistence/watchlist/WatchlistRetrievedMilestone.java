package com.tradehero.th.persistence.watchlist;

import com.tradehero.common.persistence.DTORetrievedAsyncMilestone;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: tho on 12/5/13.
 */
public class WatchlistRetrievedMilestone extends DTORetrievedAsyncMilestone<UserBaseKey, SecurityIdList, UserWatchlistPositionCache>
{
    @Inject protected Lazy<UserWatchlistPositionCache> watchlistPositionCache;

    @Inject public WatchlistRetrievedMilestone(CurrentUserId currentUserId)
    {
        this(currentUserId.toUserBaseKey());
    }

    public WatchlistRetrievedMilestone(UserBaseKey key)
    {
        super(key);
        DaggerUtils.inject(this);
    }

    @Override
    protected UserWatchlistPositionCache getCache()
    {
        return watchlistPositionCache.get();
    }

    @Override
    public void launch()
    {
        launchOwn();
    }
}
