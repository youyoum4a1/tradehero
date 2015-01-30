package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ExchangeLeaderboardDefListKey extends LeaderboardDefListKey
{
    static final String EXCHANGE = "Exchange";

    //<editor-fold desc="Constructors">
    public ExchangeLeaderboardDefListKey(@Nullable Integer page)
    {
        super(EXCHANGE, page);
    }

    public ExchangeLeaderboardDefListKey(@NonNull Bundle args)
    {
        super(args);
        if (!key.equals(EXCHANGE))
        {
            throw new IllegalStateException("Key cannot be " + key);
        }
    }
    //</editor-fold>
}
