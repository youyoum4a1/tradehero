package com.tradehero.th.api.leaderboard;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;

/** Created with IntelliJ IDEA. User: tho Date: 10/16/13 Time: 10:33 AM Copyright (c) TradeHero */
public class LeaderboardDefListKey extends AbstractIntegerDTOKey
{
    public static final String BUNDLE_KEY = LeaderboardDefListKey.class.getName();
    public static final Integer ALL_LEADERBOARD_DEF = 0;

    public LeaderboardDefListKey()
    {
        super(ALL_LEADERBOARD_DEF);
    }

    public LeaderboardDefListKey(Bundle args)
    {
        super(args);
    }

    public LeaderboardDefListKey(Integer key)
    {
        super(key);
    }

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY;
    }
}
