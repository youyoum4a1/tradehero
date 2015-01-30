package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class DrillDownLeaderboardDefListKey extends LeaderboardDefListKey
{
    static final String DRILL_DOWN = "DrillDown";

    //<editor-fold desc="Constructors">
    public DrillDownLeaderboardDefListKey(@Nullable Integer page)
    {
        super(DRILL_DOWN, page);
    }

    public DrillDownLeaderboardDefListKey(@NonNull Bundle args)
    {
        super(args);
        if (!key.equals(DRILL_DOWN))
        {
            throw new IllegalStateException("Key cannot be " + key);
        }
    }
    //</editor-fold>
}
