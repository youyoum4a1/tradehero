package com.tradehero.th.api.leaderboard;

import android.os.Bundle;

/** Created with IntelliJ IDEA. User: tho Date: 10/16/13 Time: 3:26 PM Copyright (c) TradeHero */
public class LeaderboardDefSectorListKey extends LeaderboardDefListKey
{
    private static final String LEADERBOARD_DEF_SECTOR = "LEADERBOARD_DEF_SECTOR";

    //<editor-fold desc="Constructors">
    public LeaderboardDefSectorListKey()
    {
        super(LEADERBOARD_DEF_SECTOR);
    }

    public LeaderboardDefSectorListKey(Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public boolean equals(Object other)
    {
        return (other instanceof LeaderboardDefSectorListKey) && equals((LeaderboardDefSectorListKey) other);
    }
}
