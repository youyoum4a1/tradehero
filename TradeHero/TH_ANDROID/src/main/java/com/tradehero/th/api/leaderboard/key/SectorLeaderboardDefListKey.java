package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;

public class SectorLeaderboardDefListKey extends LeaderboardDefListKey
{
    static final String SECTOR = "Sector";

    //<editor-fold desc="Constructors">
    public SectorLeaderboardDefListKey()
    {
        super(SECTOR);
    }

    public SectorLeaderboardDefListKey(Bundle args)
    {
        super(args);
        if (!key.equals(SECTOR))
        {
            throw new IllegalStateException("Key cannot be " + key);
        }
    }
    //</editor-fold>
}
