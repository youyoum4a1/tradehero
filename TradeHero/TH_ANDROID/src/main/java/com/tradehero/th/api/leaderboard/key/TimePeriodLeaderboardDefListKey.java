package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;

public class TimePeriodLeaderboardDefListKey extends LeaderboardDefListKey
{
    static final String TIME_PERIOD = "TimePeriod";

    //<editor-fold desc="Constructors">
    public TimePeriodLeaderboardDefListKey()
    {
        super(TIME_PERIOD);
    }

    public TimePeriodLeaderboardDefListKey(Bundle args)
    {
        super(args);
        if (!key.equals(TIME_PERIOD))
        {
            throw new IllegalStateException("Key cannot be " + key);
        }
    }
    //</editor-fold>
}
