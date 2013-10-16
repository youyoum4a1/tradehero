package com.tradehero.th.api.leaderboard;

import android.os.Bundle;

/** Created with IntelliJ IDEA. User: tho Date: 10/16/13 Time: 3:26 PM Copyright (c) TradeHero */
public class LeaderboardDefSectorListKey extends LeaderboardDefListKey
{
    //<editor-fold desc="Constructors">
    public LeaderboardDefSectorListKey()
    {
        super();
    }

    public LeaderboardDefSectorListKey(Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public String makeKey()
    {
        return LeaderboardDefSectorListKey.class.getName() + ':' + super.makeKey();
    }
}
