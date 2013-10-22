package com.tradehero.th.api.leaderboard;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;

/** Created with IntelliJ IDEA. User: tho Date: 10/16/13 Time: 12:46 PM Copyright (c) TradeHero */
public class LeaderboardDefKey extends AbstractIntegerDTOKey
{
    private static final String BUNDLE_KEY = LeaderboardDefKey.class.getName() + ".key";
    private static final String TAG = LeaderboardDefKey.class.getName();

    //<editor-fold desc="Constructors">
    public LeaderboardDefKey(Integer key)
    {
        super(key);
    }

    public LeaderboardDefKey(Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY;
    }

    @Override public boolean equals(Object other)
    {
        return (other instanceof LeaderboardDefKey) && super.equals((LeaderboardDefKey) other);
    }

    @Override public String toString()
    {
        return String.format("%s %s", TAG, makeKey());
    }
}
