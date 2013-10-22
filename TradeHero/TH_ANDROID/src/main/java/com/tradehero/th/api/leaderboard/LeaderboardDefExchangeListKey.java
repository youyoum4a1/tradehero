package com.tradehero.th.api.leaderboard;

import android.os.Bundle;

/** Created with IntelliJ IDEA. User: tho Date: 10/16/13 Time: 3:57 PM Copyright (c) TradeHero */
public class LeaderboardDefExchangeListKey extends LeaderboardDefListKey
{
    //<editor-fold desc="Constructors">
    public LeaderboardDefExchangeListKey()
    {
        super();
    }

    public LeaderboardDefExchangeListKey(Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public boolean equals(Object other)
    {
        return (other instanceof LeaderboardDefExchangeListKey) && equals((LeaderboardDefExchangeListKey) other);
    }

    @Override public String makeKey()
    {
        return LeaderboardDefExchangeListKey.class.getName() + ':' + super.makeKey();
    }
}
