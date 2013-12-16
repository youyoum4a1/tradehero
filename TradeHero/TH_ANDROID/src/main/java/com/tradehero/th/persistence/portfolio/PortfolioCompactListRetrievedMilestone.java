package com.tradehero.th.persistence.portfolio;

import com.tradehero.common.persistence.DTORetrievedAsyncMilestone;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 6:13 PM To change this template use File | Settings | File Templates. */
public class PortfolioCompactListRetrievedMilestone extends DTORetrievedAsyncMilestone<UserBaseKey, OwnedPortfolioIdList, PortfolioCompactListCache>
{
    public static final String TAG = PortfolioCompactListRetrievedMilestone.class.getSimpleName();

    @Inject Lazy<PortfolioCompactListCache> portfolioCompactListCache;

    @Inject public PortfolioCompactListRetrievedMilestone(CurrentUserBaseKeyHolder currentUserBaseKeyHolder)
    {
        this(currentUserBaseKeyHolder.getCurrentUserBaseKey());
    }

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
        launchOwn();
    }
}
