package com.tradehero.th.fragments.portfolio.header;

import android.support.annotation.DimenRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;

/**
 * Class creating instances of PortfolioHeaderView based on which arguments are passed to the PositionListFragment
 */
public class PortfolioHeaderFactory
{
    @LayoutRes public static int layoutIdFor(
            @NonNull GetPositionsDTOKey getPositionsDTOKey,
            @Nullable PortfolioCompactDTO portfolioCompactDTO,
            @NonNull CurrentUserId currentUserId)
    {
        if (portfolioCompactDTO != null && portfolioCompactDTO.isFx())
        {
            return R.layout.portfolio_header_fx_current_user_view;
        }
        else
        {
            if (getPositionsDTOKey instanceof LeaderboardMarkUserId)
            {
                return R.layout.portfolio_header_other_user_view;
            }
            else if (getPositionsDTOKey instanceof OwnedPortfolioId)
            {
                return layoutIdForStocks((OwnedPortfolioId) getPositionsDTOKey, currentUserId);
            }
        }
        throw new IllegalArgumentException("Unhandled getPositionDTOKey type " + getPositionsDTOKey.getClass());
    }

    @LayoutRes public static int layoutIdForLive()
    {
        return R.layout.live_portfolio_header_view;
    }

    @DimenRes public static int layoutHeightFor(
            @LayoutRes int layoutRes)
    {
        switch (layoutRes)
        {
            case R.layout.portfolio_header_fx_current_user_view:
                return R.dimen.fx_positions_header_height;
            case R.layout.portfolio_header_other_user_view:
                return R.dimen.stock_positions_header_other_height;
            case R.layout.portfolio_header_current_user_view:
                return R.dimen.stock_positions_header_mine_height;
            case R.layout.live_portfolio_header_view:
                return R.dimen.stock_positions_header_mine_height;
            default:
                throw new IllegalArgumentException("Unhandled layoutRes " + layoutRes);
        }
    }

    @LayoutRes static int layoutIdForStocks(
            @NonNull OwnedPortfolioId ownedPortfolioId,
            @NonNull CurrentUserId currentUserId)
    {
        return layoutIdFor(ownedPortfolioId.getUserBaseKey(), currentUserId);
    }

    @LayoutRes public static int layoutIdFor(
            @NonNull UserBaseKey userBaseKey,
            @NonNull CurrentUserId currentUserId)
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
