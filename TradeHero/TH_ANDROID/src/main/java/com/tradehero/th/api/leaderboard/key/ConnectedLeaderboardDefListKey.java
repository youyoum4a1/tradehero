package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ConnectedLeaderboardDefListKey extends LeaderboardDefListKey
{
    static final String CONNECTED = "Connected";

    //<editor-fold desc="Constructors">
    public ConnectedLeaderboardDefListKey(@Nullable Integer page)
    {
        super(CONNECTED, page);
    }

    public ConnectedLeaderboardDefListKey(@NonNull Bundle args)
    {
        super(args);
        if (!key.equals(CONNECTED))
        {
            throw new IllegalStateException("Key cannot be " + key);
        }
    }
    //</editor-fold>
}
