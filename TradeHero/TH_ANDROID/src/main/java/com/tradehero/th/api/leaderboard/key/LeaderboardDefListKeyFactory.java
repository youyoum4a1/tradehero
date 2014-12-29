package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import javax.inject.Inject;

public class LeaderboardDefListKeyFactory
{
    //<editor-fold desc="Constructors">
    @Inject public LeaderboardDefListKeyFactory()
    {
        super();
    }
    //</editor-fold>

    public LeaderboardDefListKey create(@NonNull Bundle args)
    {
        switch (args.getString(LeaderboardDefListKey.BUNDLE_KEY_KEY))
        {
            case ConnectedLeaderboardDefListKey.CONNECTED:
                return new ConnectedLeaderboardDefListKey(args);

            case DrillDownLeaderboardDefListKey.DRILL_DOWN:
                return new DrillDownLeaderboardDefListKey(args);

            case ExchangeLeaderboardDefListKey.EXCHANGE:
                return new ExchangeLeaderboardDefListKey(args);

            case MostSkilledLeaderboardDefListKey.MOST_SKILLED:
                return new MostSkilledLeaderboardDefListKey(args);

            case MostSkilledContainerLeaderboardDefListKey.MOST_SKILLED_CONTAINER:
                return new MostSkilledContainerLeaderboardDefListKey(args);

            case SectorLeaderboardDefListKey.SECTOR:
                return new SectorLeaderboardDefListKey(args);

            case TimePeriodLeaderboardDefListKey.TIME_PERIOD:
                return new TimePeriodLeaderboardDefListKey(args);

            default:
                throw new IllegalArgumentException("Unhandled key value " + args.getString(LeaderboardDefListKey.BUNDLE_KEY_KEY));
        }
    }
}
