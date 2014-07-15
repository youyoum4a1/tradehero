package com.tradehero.th.persistence.portfolio;

import com.tradehero.common.persistence.DTORetrievedAsyncMilestoneNew;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

@Deprecated // We should always ask for the value from the cache
public class PortfolioCompactListRetrievedMilestone extends DTORetrievedAsyncMilestoneNew<UserBaseKey, PortfolioCompactDTOList, PortfolioCompactListCache>
{
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

    public boolean hasCompacts(PortfolioCompactDTOList compactDTOs)
    {
        if (compactDTOs == null)
        {
            return false;
        }
        for (PortfolioCompactDTO portfolioCompactDTO : compactDTOs)
        {
            if (portfolioCompactDTO == null)
            {
                return false;
            }
        }
        return true;
    }
}
