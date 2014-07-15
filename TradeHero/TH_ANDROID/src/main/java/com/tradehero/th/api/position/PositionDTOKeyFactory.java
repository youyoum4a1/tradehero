package com.tradehero.th.api.position;

import android.os.Bundle;
import com.tradehero.th.api.leaderboard.position.OwnedLeaderboardPositionId;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PositionDTOKeyFactory
{
    //<editor-fold desc="Constructors">
    @Inject public PositionDTOKeyFactory()
    {
        super();
    }
    //</editor-fold>

    @Nullable public PositionDTOKey createFrom(@NotNull Bundle args)
    {
        if (OwnedPositionId.isOwnedPositionId(args))
        {
            return new OwnedPositionId(args);
        }
        else if (OwnedLeaderboardPositionId.isOwnedLeaderboardPositionId(args))
        {
            return new OwnedLeaderboardPositionId(args);
        }
        return null;
    }
}
