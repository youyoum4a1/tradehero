package com.androidth.general.api.position;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.androidth.general.api.leaderboard.position.OwnedLeaderboardPositionId;

public class PositionDTOKeyFactory
{
    @NonNull public static PositionDTOKey createFrom(@NonNull Bundle args)
    {
        if (OwnedPositionId.isOwnedPositionId(args))
        {
            return new OwnedPositionId(args);
        }
        else if (OwnedLeaderboardPositionId.isOwnedLeaderboardPositionId(args))
        {
            return new OwnedLeaderboardPositionId(args);
        }
        throw new IllegalArgumentException("Bundle does not contain a PositionDTOKey");
    }
}