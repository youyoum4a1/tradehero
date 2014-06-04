package com.tradehero.th.api.position;

import android.os.Bundle;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import javax.inject.Inject;

public class GetPositionsDTOKeyFactory
{
    //<editor-fold desc="Constructors">
    @Inject public GetPositionsDTOKeyFactory()
    {
        super();
    }
    //</editor-fold>

    public GetPositionsDTOKey createFrom(Bundle args)
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
