package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;

public class MostSkilledContainerLeaderboardDefListKey extends LeaderboardDefListKey
{
    static final String MOST_SKILLED_CONTAINER = "MostSkilledContainer";

    //<editor-fold desc="Constructors">
    public MostSkilledContainerLeaderboardDefListKey()
    {
        super(MOST_SKILLED_CONTAINER);
    }

    public MostSkilledContainerLeaderboardDefListKey(Bundle args)
    {
        super(args);
        if (!key.equals(MOST_SKILLED_CONTAINER))
        {
            throw new IllegalStateException("Key cannot be " + key);
        }
    }
    //</editor-fold>
}
