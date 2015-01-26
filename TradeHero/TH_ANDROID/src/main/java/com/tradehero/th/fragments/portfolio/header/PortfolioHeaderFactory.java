package com.tradehero.th.fragments.portfolio.header;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
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
    @NonNull protected final CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    @Inject public PortfolioHeaderFactory(@NonNull CurrentUserId currentUserId)
    {
        this.currentUserId = currentUserId;
    }
    //</editor-fold>

    @LayoutRes public int layoutIdFor(
            @NonNull GetPositionsDTOKey getPositionsDTOKey,
            @NonNull PortfolioCompactDTO portfolioCompactDTO)
    {
        if (portfolioCompactDTO.isFx())
        {
            // TODO more tests
            return layoutIdForFx(portfolioCompactDTO.getOwnedPortfolioId());
        }
        else
        {
            if (getPositionsDTOKey instanceof LeaderboardMarkUserId)
            {
                return layoutIdForStocks((LeaderboardMarkUserId) getPositionsDTOKey);
            }
            else if (getPositionsDTOKey instanceof OwnedPortfolioId)
            {
                return layoutIdForStocks((OwnedPortfolioId) getPositionsDTOKey);
            }
        }
        throw new IllegalArgumentException("Unhandled getPositionDTOKey type " + getPositionsDTOKey.getClass());
    }

    @LayoutRes protected int layoutIdForFx(@NonNull OwnedPortfolioId ownedPortfolioId)
    {
        // TODO more tests
        return R.layout.portfolio_header_fx_current_user_view;
    }

    @LayoutRes protected int layoutIdForStocks(LeaderboardMarkUserId leaderboardMarkUserId)
    {
        // TODO check whether we need to see this is current user or not
        return R.layout.portfolio_header_other_user_view;
    }

    @LayoutRes protected int layoutIdForStocks(@NonNull OwnedPortfolioId ownedPortfolioId)
    {
        return layoutIdFor(ownedPortfolioId.getUserBaseKey());
    }

    @LayoutRes public int layoutIdFor(@NonNull UserBaseKey userBaseKey)
    {
        // TODO distinguish Fx portfolios
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
