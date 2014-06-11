package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;

public class ExchangeLeaderboardDefListKey extends LeaderboardDefListKey
{
    static final String EXCHANGE = "Exchange";

    //<editor-fold desc="Constructors">
    public ExchangeLeaderboardDefListKey()
    {
        super(EXCHANGE);
    }

    public ExchangeLeaderboardDefListKey(Bundle args)
    {
        super(args);
        if (!key.equals(EXCHANGE))
        {
            throw new IllegalStateException("Key cannot be " + key);
        }
    }
    //</editor-fold>
}
