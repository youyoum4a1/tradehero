package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class LeaderboardDefListKeyFactory
{
    @NonNull public static LeaderboardDefListKey create(@NonNull Bundle args)
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

            case SectorLeaderboardDefListKey.SECTOR:
                return new SectorLeaderboardDefListKey(args);

            case TimePeriodLeaderboardDefListKey.TIME_PERIOD:
                return new TimePeriodLeaderboardDefListKey(args);

            default:
                throw new IllegalArgumentException("Unhandled key value " + args.getString(LeaderboardDefListKey.BUNDLE_KEY_KEY));
        }
    }

    @NonNull public static LeaderboardDefListKey create(@NonNull LeaderboardDefListKey origin, @Nullable Integer page)
    {
        if (origin instanceof ConnectedLeaderboardDefListKey)
        {
            return new ConnectedLeaderboardDefListKey(page);
        }
        if (origin instanceof DrillDownLeaderboardDefListKey)
        {
            return new DrillDownLeaderboardDefListKey(page);
        }
        if (origin instanceof ExchangeLeaderboardDefListKey)
        {
            return new ExchangeLeaderboardDefListKey(page);
        }
        if (origin instanceof MostSkilledLeaderboardDefListKey)
        {
            return new MostSkilledLeaderboardDefListKey(page);
        }
        if (origin instanceof SectorLeaderboardDefListKey)
        {
            return new SectorLeaderboardDefListKey(page);
        }
        if (origin instanceof TimePeriodLeaderboardDefListKey)
        {
            return new TimePeriodLeaderboardDefListKey(page);
        }
        throw new IllegalArgumentException("Unhandled type " + origin.getClass());
    }
}
