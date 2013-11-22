package com.tradehero.th.persistence.portfolio;

import com.tradehero.common.persistence.DTORetrievedMilestone;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 6:13 PM To change this template use File | Settings | File Templates. */
public class PortfolioCompactListRetrievedMilestone extends DTORetrievedMilestone<UserBaseKey, OwnedPortfolioIdList, PortfolioCompactListCache>
{
    public static final String TAG = PortfolioCompactListRetrievedMilestone.class.getSimpleName();

    @Inject Lazy<PortfolioCompactListCache> portfolioCompactListCache;

    public PortfolioCompactListRetrievedMilestone(UserBaseKey key)
    {
        super(key);
        DaggerUtils.inject(this);
    }

    @Override protected PortfolioCompactListCache getCache()
    {
        return portfolioCompactListCache.get();
    }

    @Override public void launch()
    {
        launchInternal();
    }
}
