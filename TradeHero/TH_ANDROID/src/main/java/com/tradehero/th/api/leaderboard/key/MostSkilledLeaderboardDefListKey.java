package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;

public class MostSkilledLeaderboardDefListKey extends LeaderboardDefListKey
{
    static final String MOST_SKILLED = "MostSkilled";

    //<editor-fold desc="Constructors">
    public MostSkilledLeaderboardDefListKey()
    {
        super(MOST_SKILLED);
    }

    public MostSkilledLeaderboardDefListKey(Bundle args)
    {
        super(args);
        if (!key.equals(MOST_SKILLED))
        {
            throw new IllegalStateException("Key cannot be " + key);
        }
    }
    //</editor-fold>
}
