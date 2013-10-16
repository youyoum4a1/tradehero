package com.tradehero.th.api.leaderboard;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;

/** Created with IntelliJ IDEA. User: tho Date: 10/16/13 Time: 10:22 AM Copyright (c) TradeHero */
public class LeaderboardKey extends AbstractIntegerDTOKey
{

    private static final String BUNDLE_KEY = LeaderboardKey.class.getName() + ".key";

    public LeaderboardKey(Integer key)
    {
        super(key);
    }

    public LeaderboardKey(Bundle args)
    {
        super(args);
    }

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY;
    }
}
