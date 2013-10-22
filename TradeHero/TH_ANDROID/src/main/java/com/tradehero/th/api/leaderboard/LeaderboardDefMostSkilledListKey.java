package com.tradehero.th.api.leaderboard;

/** Created with IntelliJ IDEA. User: tho Date: 10/16/13 Time: 3:17 PM Copyright (c) TradeHero */
public class LeaderboardDefMostSkilledListKey extends LeaderboardDefListKey
{
    @Override public boolean equals(Object other)
    {
        return (other instanceof LeaderboardDefMostSkilledListKey) && super.equals((LeaderboardDefMostSkilledListKey) other);
    }

    @Override public String makeKey()
    {
        return LeaderboardDefMostSkilledListKey.class.getName() + ':' + super.makeKey();
    }
}
