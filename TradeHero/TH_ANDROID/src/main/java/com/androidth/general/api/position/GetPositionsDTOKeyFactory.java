package com.androidth.general.api.position;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.api.leaderboard.position.LeaderboardMarkUserId;
import com.androidth.general.api.leaderboard.position.PagedLeaderboardMarkUserId;
import com.androidth.general.api.leaderboard.position.PerPagedLeaderboardMarkUserId;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.portfolio.PagedOwnedPortfolioId;
import com.androidth.general.api.portfolio.PerPagedOwnedPortfolioId;
import com.androidth.general.api.trade.OwnedTradeId;

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
