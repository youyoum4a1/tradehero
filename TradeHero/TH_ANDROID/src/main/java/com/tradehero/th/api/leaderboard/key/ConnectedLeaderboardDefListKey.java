package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;

public class ConnectedLeaderboardDefListKey extends LeaderboardDefListKey
{
    static final String CONNECTED = "Connected";

    //<editor-fold desc="Constructors">
    public ConnectedLeaderboardDefListKey()
    {
        super(CONNECTED);
    }

    public ConnectedLeaderboardDefListKey(Bundle args)
    {
        super(args);
        if (!key.equals(CONNECTED))
        {
            throw new IllegalStateException("Key cannot be " + key);
        }
    }
    //</editor-fold>
}
