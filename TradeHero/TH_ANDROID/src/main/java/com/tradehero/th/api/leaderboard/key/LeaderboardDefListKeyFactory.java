package com.tradehero.th.api.leaderboard.key;

import javax.inject.Inject;

public class LeaderboardDefListKeyFactory
{
    private static final String CONNECTED = "connected";
    private static final String MOST_SKILLED = "MostSkilled";
    private static final String TIME_PERIOD = "TimePeriod";
    private static final String DRILL_DOWN = "drillDown";
    private static final String SECTOR = "Sector";
    private static final String EXCHANGE = "Exchange";

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardDefListKeyFactory()
    {
        super();
    }
    //</editor-fold>

    public LeaderboardDefListKey createConnected()
    {
        return new LeaderboardDefListKey(CONNECTED);
    }

    public LeaderboardDefListKey createDrillDown()
    {
        return new LeaderboardDefListKey(DRILL_DOWN);
    }

    public LeaderboardDefListKey createMostSkilled()
    {
        return new LeaderboardDefListKey(MOST_SKILLED);
    }

    public LeaderboardDefListKey createSector()
    {
        return new LeaderboardDefListKey(SECTOR);
    }

    public LeaderboardDefListKey createTimePeriod()
    {
        return new LeaderboardDefListKey(TIME_PERIOD);
    }

    public LeaderboardDefListKey createExchange()
    {
        return new LeaderboardDefListKey(EXCHANGE);
    }


}
