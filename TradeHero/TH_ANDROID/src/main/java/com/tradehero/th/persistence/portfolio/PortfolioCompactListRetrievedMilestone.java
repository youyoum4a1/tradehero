package com.tradehero.th.persistence.portfolio;

import com.tradehero.common.persistence.DTORetrievedAsyncMilestone;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 6:13 PM To change this template use File | Settings | File Templates. */
public class PortfolioCompactListRetrievedMilestone extends DTORetrievedAsyncMilestone<UserBaseKey, OwnedPortfolioIdList, PortfolioCompactListCache>
{
    public static final String TAG = PortfolioCompactListRetrievedMilestone.class.getSimpleName();

    @Inject PortfolioCompactListCache portfolioCompactListCache;
    @Inject PortfolioCompactCache portfolioCompactCache;

    @Inject public PortfolioCompactListRetrievedMilestone(CurrentUserId currentUserId)
    {
        this(currentUserId.toUserBaseKey());
    }

    public PortfolioCompactListRetrievedMilestone(UserBaseKey key)
    {
        super(key);
        DaggerUtils.inject(this);
    }

    @Override protected PortfolioCompactListCache getCache()
    {
        return portfolioCompactListCache;
    }

    @Override public void launch()
    {
        launchOwn();
    }

    @Override public boolean isComplete()
    {
        return super.isComplete() && hasCompacts(portfolioCompactListCache.get(key));
    }

    public boolean hasCompacts(OwnedPortfolioIdList keyList)
    {
        if (keyList == null)
        {
            return false;
        }
        for (OwnedPortfolioId id : keyList)
        {
            if (portfolioCompactCache.get(id.getPortfolioIdKey()) == null)
            {
                return false;
            }
        }
        return true;
    }
}
