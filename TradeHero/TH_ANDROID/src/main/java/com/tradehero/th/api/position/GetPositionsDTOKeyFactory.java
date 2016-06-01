package com.ayondo.academy.api.position;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ayondo.academy.api.leaderboard.position.LeaderboardMarkUserId;
import com.ayondo.academy.api.leaderboard.position.PagedLeaderboardMarkUserId;
import com.ayondo.academy.api.leaderboard.position.PerPagedLeaderboardMarkUserId;
import com.ayondo.academy.api.portfolio.OwnedPortfolioId;
import com.ayondo.academy.api.portfolio.PagedOwnedPortfolioId;
import com.ayondo.academy.api.portfolio.PerPagedOwnedPortfolioId;
import com.ayondo.academy.api.trade.OwnedTradeId;

public class GetPositionsDTOKeyFactory
{
    @Nullable public static GetPositionsDTOKey createFrom(@NonNull Bundle args)
    {
        if (PerPagedOwnedPortfolioId.isPagedOwnedPortfolioId(args))
        {
            return new PerPagedOwnedPortfolioId(args);
        }
        else if (PagedOwnedPortfolioId.isPagedOwnedPortfolioId(args))
        {
            return new PagedOwnedPortfolioId(args);
        }
        else if (OwnedTradeId.isOwnedTradeId(args))
        {
            return new OwnedTradeId(args);
        }
        else if (OwnedPositionId.isOwnedPositionId(args))
        {
            return new OwnedPositionId(args);
        }
        else if (OwnedPortfolioId.isOwnedPortfolioId(args))
        {
            return new OwnedPortfolioId(args);
        }
        else if (PerPagedLeaderboardMarkUserId.isPerPagedLeaderboardMarkUserId(args))
        {
            return new PerPagedLeaderboardMarkUserId(args);
        }
        else if (PagedLeaderboardMarkUserId.isPagedLeaderboardMarkUserId(args))
        {
            return new PagedLeaderboardMarkUserId(args);
        }
        else if (LeaderboardMarkUserId.isLeaderboardMarkUserId(args))
        {
            return new LeaderboardMarkUserId(args);
        }
        return null;
    }
}
