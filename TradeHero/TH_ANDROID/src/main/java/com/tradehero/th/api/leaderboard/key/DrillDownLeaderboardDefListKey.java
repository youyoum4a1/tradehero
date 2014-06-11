package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;

public class DrillDownLeaderboardDefListKey extends LeaderboardDefListKey
{
    static final String DRILL_DOWN = "DrillDown";

    //<editor-fold desc="Constructors">
    public DrillDownLeaderboardDefListKey()
    {
        super(DRILL_DOWN);
    }

    public DrillDownLeaderboardDefListKey(Bundle args)
    {
        super(args);
        if (!key.equals(DRILL_DOWN))
        {
            throw new IllegalStateException("Key cannot be " + key);
        }
    }
    //</editor-fold>
}
