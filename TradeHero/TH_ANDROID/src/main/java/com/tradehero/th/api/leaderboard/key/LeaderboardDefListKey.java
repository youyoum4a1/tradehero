package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractStringDTOKey;

public class LeaderboardDefListKey extends AbstractStringDTOKey
{
    private static final String BUNDLE_KEY_KEY = LeaderboardDefKey.class.getName() + ".key";
    private static final String ALL_LEADERBOARD_DEF = "ALL_LEADERBOARD_DEF";
    private static final String CONNECTED_LEADERBOARD_DEF = "CONNECTED_LEADERBOARD_DEF";
    private static final String DRILL_DOWN_LEADERBOARD_DEF = "DRILL_DOWN_LEADERBOARD_DEF";
    private static final String ALL_LEADERBOARD_DEF_MOST_SKILLED = "ALL_LEADERBOARD_DEF_MOST_SKILLED";
    private static final String LEADERBOARD_DEF_SECTOR = "LEADERBOARD_DEF_SECTOR";
    private static final String ALL_LEADERBOARD_DEF_TIME_PERIOD = "ALL_LEADERBOARD_DEF_TIME_PERIOD";
    private static final String LEADERBOARD_DEF_EXCHANGE = "LEADERBOARD_DEF_EXCHANGE";

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

    public static LeaderboardDefListKey getConnected()
    {
        return new LeaderboardDefListKey(CONNECTED_LEADERBOARD_DEF);
    }

    public static LeaderboardDefListKey getDrillDown()
    {
        return new LeaderboardDefListKey(DRILL_DOWN_LEADERBOARD_DEF);
    }

    public static LeaderboardDefListKey getMostSkilled()
    {
        return new LeaderboardDefListKey(ALL_LEADERBOARD_DEF_MOST_SKILLED);
    }

    public static LeaderboardDefListKey getSector()
    {
        return new LeaderboardDefListKey(LEADERBOARD_DEF_SECTOR);
    }

    public static LeaderboardDefListKey getTimePeriod()
    {
        return new LeaderboardDefListKey(ALL_LEADERBOARD_DEF_TIME_PERIOD);
    }

    public static LeaderboardDefListKey getExchange()
    {
        return new LeaderboardDefListKey(LEADERBOARD_DEF_EXCHANGE);
    }
}
