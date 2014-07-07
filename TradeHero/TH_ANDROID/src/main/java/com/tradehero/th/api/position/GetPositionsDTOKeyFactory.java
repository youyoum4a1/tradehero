package com.tradehero.th.api.position;

import android.os.Bundle;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
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
        // TODO think about creating child classes of OwnedPortfolioId?
        if (OwnedPortfolioId.isOwnedPortfolioId(args))
        {
            return new OwnedPortfolioId(args);
        }
        else if (LeaderboardMarkUserId.isLeaderboardMarkUserId(args))
        {
            return new LeaderboardMarkUserId(args);
        }
        return null;
    }
}
