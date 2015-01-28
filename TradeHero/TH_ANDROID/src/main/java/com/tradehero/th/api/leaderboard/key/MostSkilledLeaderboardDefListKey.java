package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;
import android.support.annotation.Nullable;

public class MostSkilledLeaderboardDefListKey extends LeaderboardDefListKey
{
    static final String MOST_SKILLED = "MostSkilled";

    //<editor-fold desc="Constructors">
    public MostSkilledLeaderboardDefListKey(@Nullable Integer page)
    {
        super(MOST_SKILLED, page);
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
