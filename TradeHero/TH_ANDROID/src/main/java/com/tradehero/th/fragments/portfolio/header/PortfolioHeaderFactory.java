package com.tradehero.th.fragments.portfolio.header;

import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Singleton Class creating instances of PortfolioHeaderView based on which arguments are passed to the PositionListFragment
 */
@Singleton public class PortfolioHeaderFactory
{
    @Inject protected CurrentUserId currentUserId;

    public int layoutIdFor(GetPositionsDTOKey getPositionsDTOKey)
    {
        if (getPositionsDTOKey instanceof LeaderboardMarkUserId)
        {
            return layoutIdFor((LeaderboardMarkUserId) getPositionsDTOKey);
        }
        else if (getPositionsDTOKey instanceof OwnedPortfolioId)
        {
            return layoutIdFor((OwnedPortfolioId) getPositionsDTOKey);
        }
        throw new IllegalArgumentException("Unhandled getPositionDTOKey type " + getPositionsDTOKey.getClass());
    }

    protected int layoutIdFor(LeaderboardMarkUserId leaderboardMarkUserId)
    {
        // TODO check whether we need to see this is current user or not
        return R.layout.portfolio_header_other_user_view;
    }

    protected int layoutIdFor(OwnedPortfolioId ownedPortfolioId)
    {
        return layoutIdFor(ownedPortfolioId.getUserBaseKey());
    }

    public int layoutIdFor(UserBaseKey userBaseKey)
    {
        if (userBaseKey.equals(currentUserId.toUserBaseKey()))
        {
            return R.layout.portfolio_header_current_user_view;
        }
        else
        {
            return R.layout.portfolio_header_other_user_view;
        }
    }
}
