package com.tradehero.th.api.leaderboard;

import android.os.Bundle;

/** Created with IntelliJ IDEA. User: tho Date: 10/16/13 Time: 3:17 PM Copyright (c) TradeHero */
public class LeaderboardDefMostSkilledListKey extends LeaderboardDefListKey
{
    //<editor-fold desc="Constructors">
    public LeaderboardDefMostSkilledListKey()
    {
        super();
    }
    //</editor-fold>

    @Override public boolean equals(Object other)
    {
        return (other instanceof LeaderboardDefMostSkilledListKey) && equals((LeaderboardDefMostSkilledListKey) other);
    }
}
