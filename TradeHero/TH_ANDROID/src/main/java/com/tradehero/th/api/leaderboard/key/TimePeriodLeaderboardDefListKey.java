package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;
import android.support.annotation.Nullable;

public class TimePeriodLeaderboardDefListKey extends LeaderboardDefListKey
{
    static final String TIME_PERIOD = "TimePeriod";

    //<editor-fold desc="Constructors">
    public TimePeriodLeaderboardDefListKey(@Nullable Integer page)
    {
        super(TIME_PERIOD, page);
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
