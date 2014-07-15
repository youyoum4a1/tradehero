package com.tradehero.th.api.position;

import android.os.Bundle;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.leaderboard.position.PagedLeaderboardMarkUserId;
import com.tradehero.th.api.leaderboard.position.PerPagedLeaderboardMarkUserId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PagedOwnedPortfolioId;
import com.tradehero.th.api.portfolio.PerPagedOwnedPortfolioId;
import com.tradehero.th.api.trade.OwnedTradeId;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GetPositionsDTOKeyFactory
{
    //<editor-fold desc="Constructors">
    @Inject public GetPositionsDTOKeyFactory()
    {
        super();
    }
    //</editor-fold>

    @Nullable public GetPositionsDTOKey createFrom(@NotNull Bundle args)
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
