package com.tradehero.th.api.leaderboard;

import android.os.Bundle;

/** Created with IntelliJ IDEA. User: tho Date: 10/16/13 Time: 3:24 PM Copyright (c) TradeHero */
public class LeaderboardDefTimePeriodListKey extends LeaderboardDefListKey
{
    @Override public boolean equals(Object other)
    {
        return (other instanceof LeaderboardDefTimePeriodListKey) && super.equals((LeaderboardDefTimePeriodListKey) other);
    }

    @Override public String makeKey()
    {
        return LeaderboardDefTimePeriodListKey.class.getName() + ':' + super.makeKey();
    }
}
