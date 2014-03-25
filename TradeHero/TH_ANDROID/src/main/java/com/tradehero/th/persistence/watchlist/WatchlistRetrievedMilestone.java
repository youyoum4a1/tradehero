package com.tradehero.th.persistence.watchlist;

import com.tradehero.common.persistence.DTORetrievedAsyncMilestone;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: tho on 12/5/13.
 */
public class WatchlistRetrievedMilestone extends DTORetrievedAsyncMilestone<UserBaseKey, SecurityIdList, UserWatchlistPositionCache>
{
    @Inject protected UserWatchlistPositionCache watchlistPositionCache;
    @Inject protected SecurityCompactCache securityCompactCache;

    @Inject public WatchlistRetrievedMilestone(CurrentUserId currentUserId)
    {
        this(currentUserId.toUserBaseKey());
    }

    public WatchlistRetrievedMilestone(UserBaseKey key)
    {
        super(key);
        DaggerUtils.inject(this);
    }

    @Override protected UserWatchlistPositionCache getCache()
    {
        return watchlistPositionCache;
    }

    @Override public void launch()
    {
        launchOwn();
    }

    @Override public boolean isComplete()
    {
        return super.isComplete() && hasDTOs(watchlistPositionCache.get(key));
    }

    public boolean hasDTOs(SecurityIdList keyList)
    {
        if (keyList == null)
        {
            return false;
        }
        for (SecurityId id : keyList)
        {
            if (securityCompactCache.get(id) == null)
            {
                return false;
            }
        }
        return true;
    }
}
