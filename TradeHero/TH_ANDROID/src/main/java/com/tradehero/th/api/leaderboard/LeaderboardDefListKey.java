package com.tradehero.th.api.leaderboard;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractStringDTOKey;

/** Created with IntelliJ IDEA. User: tho Date: 10/16/13 Time: 10:33 AM Copyright (c) TradeHero */
public class LeaderboardDefListKey extends AbstractStringDTOKey
{
    private static final String BUNDLE_KEY_KEY = LeaderboardDefKey.class.getName() + ".key";
    private static final String ALL_LEADERBOARD_DEF = "ALL_LEADERBOARD_DEF";

    //<editor-fold desc="Constructors">
    public LeaderboardDefListKey()
    {
        super(ALL_LEADERBOARD_DEF);
    }

    public LeaderboardDefListKey(String key)
    {
        super(key);
    }

    public LeaderboardDefListKey(Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
