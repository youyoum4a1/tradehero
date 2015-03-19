package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class LeaderboardDefListKeyFactory
{
    //<editor-fold desc="Constructors">
    @Inject public LeaderboardDefListKeyFactory()
    {
        super();
    }
    //</editor-fold>

    public LeaderboardDefListKey create(@NotNull Bundle args)
    {
        switch (args.getString(LeaderboardDefListKey.BUNDLE_KEY_KEY))
        {
            case ConnectedLeaderboardDefListKey.CONNECTED:
                return new ConnectedLeaderboardDefListKey(args);

            case DrillDownLeaderboardDefListKey.DRILL_DOWN:
                return new DrillDownLeaderboardDefListKey(args);

            case ExchangeLeaderboardDefListKey.EXCHANGE:
                return new ExchangeLeaderboardDefListKey(args);

            case SectorLeaderboardDefListKey.SECTOR:
                return new SectorLeaderboardDefListKey(args);

            case TimePeriodLeaderboardDefListKey.TIME_PERIOD:
                return new TimePeriodLeaderboardDefListKey(args);

            default:
                throw new IllegalArgumentException("Unhandled key value " + args.getString(LeaderboardDefListKey.BUNDLE_KEY_KEY));
        }
    }
}
