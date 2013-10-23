package com.tradehero.th.api.leaderboard;

import android.os.Bundle;

/** Created with IntelliJ IDEA. User: tho Date: 10/16/13 Time: 3:24 PM Copyright (c) TradeHero */
public class LeaderboardDefTimePeriodListKey extends LeaderboardDefListKey
{
    //<editor-fold desc="Constructors">
    public LeaderboardDefTimePeriodListKey()
    {
        super();
    }
    //</editor-fold>

    @Override public boolean equals(Object other)
    {
        return (other instanceof LeaderboardDefTimePeriodListKey) && equals((LeaderboardDefTimePeriodListKey) other);
    }
}
